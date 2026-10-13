package com.kisanmitra.service;

import com.kisanmitra.dto.SupplyDemandOverviewResponseDto;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CropSupplyDemandServiceTest {

    @Mock
    private CropRepository cropRepository;

    @Mock
    private CropHistoricalProductionRepository productionRepository;

    @Mock
    private MspPriceNotificationRepository mspRepository;

    @Mock
    private PublicBufferStockRepository bufferStockRepository;

    @Mock
    private PublicProcurementRepository procurementRepository;

    @Mock
    private MarketDataProvider marketDataProvider;

    @InjectMocks
    private CropSupplyDemandService supplyDemandService;

    private Crop testCrop;

    @BeforeEach
    void setUp() {
        testCrop = new Crop("Rice", "Oryza sativa", "Staple food crop");
        testCrop.setId(1L);
    }

    @Test
    void testDemandStatusIsStrictlyInsufficientData() {
        when(cropRepository.findByNameIgnoreCase("Rice")).thenReturn(Optional.of(testCrop));
        when(productionRepository.findActiveByCropAndDistrict(anyLong(), anyString(), anyString())).thenReturn(Collections.emptyList());
        when(mspRepository.findLatestActiveByCrop(anyLong())).thenReturn(Collections.emptyList());
        when(bufferStockRepository.findByCommodityOrderByDateDesc("RICE")).thenReturn(Collections.emptyList());
        when(procurementRepository.findLatestByCommodity("RICE")).thenReturn(Collections.emptyList());

        SupplyDemandOverviewResponseDto response = supplyDemandService.getSupplyDemandOverview("Rice", "Andhra Pradesh", "Krishna", "Kharif");

        assertNotNull(response.getRegionalDemandBalance());
        assertEquals("INSUFFICIENT_DATA", response.getRegionalDemandBalance().getDemandStatus());
        assertEquals("IN_DEMAND_V1", response.getRegionalDemandBalance().getMethodologyVersion());
        assertFalse(response.getRegionalDemandBalance().isCalculated());
        assertNull(response.getRegionalDemandBalance().getSurplusDeficitRatio());
        assertNull(response.getRegionalDemandBalance().getSurplusDeficitQuantity());
        assertTrue(response.getRegionalDemandBalance().getRationale().contains("NSSO HCES"));
    }

    @Test
    void testHistoricalProductionWithNullsExcludesFromAverage() {
        when(cropRepository.findByNameIgnoreCase("Rice")).thenReturn(Optional.of(testCrop));

        CropHistoricalProductionObservation obs1 = new CropHistoricalProductionObservation();
        obs1.setAgriculturalYear("2021-22");
        obs1.setSeason("Kharif");
        obs1.setProductionTonnes(new BigDecimal("1000.00"));
        obs1.setRawUnit("TONNES");
        obs1.setDataStatus("VERIFIED");
        obs1.setBoundaryVintage("STANDARD_LGD_2024");

        CropHistoricalProductionObservation obs2 = new CropHistoricalProductionObservation();
        obs2.setAgriculturalYear("2022-23");
        obs2.setSeason("Kharif");
        obs2.setProductionTonnes(null); // Missing NA year
        obs2.setRawUnit("TONNES");
        obs2.setDataStatus("NOT_REPORTED");
        obs2.setBoundaryVintage("STANDARD_LGD_2024");

        when(productionRepository.findActiveByCropAndDistrict(1L, "Andhra Pradesh", "Krishna"))
                .thenReturn(List.of(obs1, obs2));
        when(mspRepository.findLatestActiveByCrop(1L)).thenReturn(Collections.emptyList());
        when(bufferStockRepository.findByCommodityOrderByDateDesc(anyString())).thenReturn(Collections.emptyList());
        when(procurementRepository.findLatestByCommodity(anyString())).thenReturn(Collections.emptyList());

        SupplyDemandOverviewResponseDto response = supplyDemandService.getSupplyDemandOverview("Rice", "Andhra Pradesh", "Krishna", "Kharif");

        assertNotNull(response.getHistoricalProduction());
        assertEquals(2, response.getHistoricalProduction().getObservations().size());
        // 5-year average must be 1000.00, NOT 500.00 (which would happen if null was coerced to 0)
        assertEquals(new BigDecimal("1000.00"), response.getHistoricalProduction().getFiveYearAverageProductionTonnes());
    }

    @Test
    void testMspMissingNotificationDistinguishedFromUnresolvedMapping() {
        // Case 1: Unresolved crop mapping
        when(cropRepository.findByNameIgnoreCase("UnknownCrop")).thenReturn(Optional.empty());
        SupplyDemandOverviewResponseDto resp1 = supplyDemandService.getSupplyDemandOverview("UnknownCrop", "AP", "Krishna", "Kharif");
        assertEquals("UNRESOLVED_MAPPING", resp1.getMspNotification().getStatus());

        // Case 2: Mapped crop, but missing CCEA gazette notification
        when(cropRepository.findByNameIgnoreCase("Rice")).thenReturn(Optional.of(testCrop));
        when(mspRepository.findLatestActiveByCrop(1L)).thenReturn(Collections.emptyList());
        SupplyDemandOverviewResponseDto resp2 = supplyDemandService.getSupplyDemandOverview("Rice", "AP", "Krishna", "Kharif");
        assertEquals("MISSING_NOTIFICATION", resp2.getMspNotification().getStatus());
        assertTrue(resp2.getMspNotification().getStatusExplanation().contains("not yet been notified"));
    }

    @Test
    void testBufferStockPeriodMismatchReturnsNoComparison() {
        when(cropRepository.findByNameIgnoreCase("Wheat")).thenReturn(Optional.of(new Crop("Wheat", "Triticum", "Cereal")));

        PublicBufferStockObservation stockMismatch = new PublicBufferStockObservation();
        stockMismatch.setCommodity("WHEAT");
        stockMismatch.setObservationDate(LocalDate.of(2024, 8, 15)); // Non-milestone date!
        stockMismatch.setActualCentralPoolLmt(new BigDecimal("268.50"));
        stockMismatch.setTotalBufferNormLmt(new BigDecimal("205.20"));
        stockMismatch.setComparisonStatus("PERIOD_MISMATCH_NO_COMPARISON");
        stockMismatch.setSurplusDeficitLmt(null); // Must be null

        when(bufferStockRepository.findByCommodityOrderByDateDesc("WHEAT")).thenReturn(List.of(stockMismatch));
        when(procurementRepository.findLatestByCommodity("WHEAT")).thenReturn(Collections.emptyList());

        SupplyDemandOverviewResponseDto response = supplyDemandService.getSupplyDemandOverview("Wheat", "MP", "Sehore", "Rabi");

        assertNotNull(response.getBufferStocks());
        assertEquals("PERIOD_MISMATCH_NO_COMPARISON", response.getBufferStocks().getComparisonStatus());
        assertNull(response.getBufferStocks().getSurplusDeficitLmt());
    }

    @Test
    void testOfflineMandiProviderGracefullyDegrades() {
        when(cropRepository.findByNameIgnoreCase("Rice")).thenReturn(Optional.of(testCrop));
        when(marketDataProvider.fetchPrices(anyString(), anyString(), anyString(), isNull()))
                .thenThrow(new RuntimeException("Connection timed out to api.data.gov.in"));

        SupplyDemandOverviewResponseDto response = supplyDemandService.getSupplyDemandOverview("Rice", "Andhra Pradesh", "Krishna", "Kharif");

        assertNotNull(response.getMandiPrices());
        assertEquals("UNCONFIRMED_OR_OFFLINE", response.getMandiPrices().getAvailabilityStatus());
        assertTrue(response.getMandiPrices().getObservations().isEmpty());
    }
}
