package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketIntelligenceServiceTest {

    @Mock
    private MarketDataProvider marketDataProvider;

    private MarketIntelligenceService marketIntelligenceService;

    @BeforeEach
    void setUp() {
        marketIntelligenceService = new MarketIntelligenceService(marketDataProvider);
    }

    @Test
    @DisplayName("getMarketPrices should return normalized observations and considerations")
    void getMarketPrices_Success() {
        MarketObservationDto obs1 = new MarketObservationDto(
                "obs_1", "Rice", "Andhra Pradesh", "Guntur", "Guntur Mandi", "Common",
                "08/10/2026", 2200.0, 2450.0, 2350.0, "INR", "₹/quintal",
                "AGMARKNET", "https://agmarknet.gov.in", "LIVE"
        );

        when(marketDataProvider.getSupportedCrops()).thenReturn(List.of("Rice", "Wheat"));
        when(marketDataProvider.fetchPrices("Rice", "Andhra Pradesh", "Guntur", null))
                .thenReturn(List.of(obs1));

        MarketResponseDto response = marketIntelligenceService.getMarketPrices("Rice", "Andhra Pradesh", "Guntur", null);

        assertNotNull(response);
        assertEquals("Rice", response.getSelectedCrop());
        assertEquals("Andhra Pradesh", response.getSelectedState());
        assertEquals(1, response.getObservations().size());

        MarketObservationDto item = response.getObservations().get(0);
        assertEquals("Guntur Mandi", item.getMarketName());
        assertEquals(2350.0, item.getModalPrice());
        assertEquals("₹/quintal", item.getUnit());
        assertEquals("LIVE", item.getFreshnessStatus());

        assertNotNull(response.getConsiderations());
        assertTrue(response.getConsiderations().size() > 0);
        assertTrue(response.getDisclaimer().contains("decision-support only"));
    }

    @Test
    @DisplayName("Benchmark fallback records must never be labeled LIVE")
    void fallbackBenchmark_MustNeverBeTaggedLIVE() {
        MarketObservationDto benchmarkObs = new MarketObservationDto(
                "ap_guntur_rice", "Rice", "Andhra Pradesh", "Guntur", "Guntur Mandi", "Common",
                "08/10/2026", 2200.0, 2450.0, 2350.0, "INR", "₹/quintal",
                "AGMARKNET / Ministry of Agriculture (Archive)", "https://agmarknet.gov.in", "HISTORICAL_BENCHMARK"
        );

        when(marketDataProvider.getSupportedCrops()).thenReturn(List.of("Rice"));
        when(marketDataProvider.fetchPrices("Rice", "Andhra Pradesh", null, null))
                .thenReturn(List.of(benchmarkObs));

        MarketResponseDto response = marketIntelligenceService.getMarketPrices("Rice", "Andhra Pradesh", null, null);

        assertNotNull(response);
        assertEquals(1, response.getObservations().size());
        MarketObservationDto item = response.getObservations().get(0);
        assertNotEquals("LIVE", item.getFreshnessStatus(), "Fallback benchmark records must never be labeled LIVE");
        assertEquals("HISTORICAL_BENCHMARK", item.getFreshnessStatus());
    }

    @Test
    @DisplayName("compareMarkets should compute regional average and mark highest/lowest price markets")
    void compareMarkets_Success() {
        MarketObservationDto obs1 = new MarketObservationDto(
                "m1", "Rice", "Andhra Pradesh", "Guntur", "Guntur Mandi", "Common",
                "08/10/2026", 2200.0, 2400.0, 2300.0, "INR", "₹/quintal", "AGMARKNET", "url", "LIVE"
        );
        MarketObservationDto obs2 = new MarketObservationDto(
                "m2", "Rice", "Andhra Pradesh", "Krishna", "Vijayawada Market", "Sona Masuri",
                "08/10/2026", 2400.0, 2600.0, 2500.0, "INR", "₹/quintal", "AGMARKNET", "url", "LIVE"
        );

        when(marketDataProvider.fetchPrices("Rice", "Andhra Pradesh", null, null))
                .thenReturn(List.of(obs1, obs2));

        MarketComparisonResponseDto response = marketIntelligenceService.compareMarkets("Rice", "Andhra Pradesh");

        assertNotNull(response);
        assertEquals("Rice", response.getCropName());
        assertEquals("Andhra Pradesh", response.getState());
        assertEquals(2400.0, response.getAverageModalPrice());
        assertEquals(2, response.getComparisonItems().size());

        MarketComparisonItemDto item1 = response.getComparisonItems().stream().filter(i -> i.getMarketName().equals("Guntur Mandi")).findFirst().orElseThrow();
        assertEquals(-100.0, item1.getPriceDifference());
        assertTrue(item1.getIsLowestPrice());

        MarketComparisonItemDto item2 = response.getComparisonItems().stream().filter(i -> i.getMarketName().equals("Vijayawada Market")).findFirst().orElseThrow();
        assertEquals(100.0, item2.getPriceDifference());
        assertTrue(item2.getIsHighestPrice());
    }

    @Test
    @DisplayName("compareMarkets should handle empty observations cleanly")
    void compareMarkets_Empty() {
        when(marketDataProvider.fetchPrices("Rice", "Goa", null, null))
                .thenReturn(Collections.emptyList());

        MarketComparisonResponseDto response = marketIntelligenceService.compareMarkets("Rice", "Goa");

        assertNotNull(response);
        assertEquals(0.0, response.getAverageModalPrice());
        assertTrue(response.getComparisonItems().isEmpty());
        assertTrue(response.getSummaryExplanation().contains("No comparable market observations"));
    }

    @Test
    @DisplayName("getMarketTrends should set hasSufficientHistory to false and NOT fabricate trend points when history is sparse")
    void getMarketTrends_SparseData_ShouldNotFabricateTrendPoints() {
        // Return 0 or 1 observation
        when(marketDataProvider.fetchPrices("Rice", null, null, "Guntur Mandi"))
                .thenReturn(Collections.emptyList());

        MarketTrendResponseDto response = marketIntelligenceService.getMarketTrends("Rice", "Guntur Mandi");

        assertNotNull(response);
        assertEquals("Rice", response.getCropName());
        assertEquals("Guntur Mandi", response.getMarketName());
        assertFalse(response.getHasSufficientHistory(), "Sparse historical observations must mark hasSufficientHistory as false");
        assertEquals("INSUFFICIENT_DATA", response.getTrendDirection());
        assertTrue(response.getTrendPoints().isEmpty(), "No fabricated or synthetic trend points should be created");
    }

    @Test
    @DisplayName("getMarketTrends should construct trend points solely from real dated observations")
    void getMarketTrends_MultipleObservations_ShouldCalculateRealTrend() {
        MarketObservationDto obs1 = new MarketObservationDto(
                "1", "Rice", "AP", "Guntur", "Guntur Mandi", "Common",
                "01/10/2026", 2200.0, 2400.0, 2300.0, "INR", "₹/quintal", "AGMARKNET", "url", "LIVE"
        );
        MarketObservationDto obs2 = new MarketObservationDto(
                "2", "Rice", "AP", "Guntur", "Guntur Mandi", "Common",
                "08/10/2026", 2250.0, 2450.0, 2350.0, "INR", "₹/quintal", "AGMARKNET", "url", "LIVE"
        );

        when(marketDataProvider.fetchPrices("Rice", null, null, "Guntur Mandi"))
                .thenReturn(List.of(obs1, obs2));

        MarketTrendResponseDto response = marketIntelligenceService.getMarketTrends("Rice", "Guntur Mandi");

        assertNotNull(response);
        assertTrue(response.getHasSufficientHistory());
        assertEquals(2, response.getTrendPoints().size());
        assertEquals("RISING", response.getTrendDirection());
        assertEquals(2300.0, response.getTrendPoints().get(0).getModalPrice());
        assertEquals(2350.0, response.getTrendPoints().get(1).getModalPrice());
    }

    @Test
    @DisplayName("AgmarknetMarketDataProvider provenance validation should filter out invalid, generic URL, or future-dated records")
    void provider_ProvenanceValidation_FiltersUnverifiedRecords() {
        AgmarknetMarketDataProvider provider = new AgmarknetMarketDataProvider(null);
        MarketObservationDto valid = new MarketObservationDto(
                "id1", "Rice", "AP", "Guntur", "Guntur Mandi", "Common",
                "08/10/2026", 2000.0, 2400.0, 2200.0, "INR", "₹/quintal",
                "AGMARKNET Archive", "https://agmarknet.gov.in/SearchCrosstab.aspx?Tx_Commodity=Rice&Tx_State=AP", "RECENT_OBSERVATION"
        );
        MarketObservationDto invalidMissingUrl = new MarketObservationDto(
                "id2", "Rice", "AP", "Guntur", "Guntur Mandi", "Common",
                "08/10/2026", 2000.0, 2400.0, 2200.0, "INR", "₹/quintal",
                "AGMARKNET Archive", null, "RECENT_OBSERVATION"
        );
        MarketObservationDto invalidGenericUrl = new MarketObservationDto(
                "id3", "Rice", "AP", "Guntur", "Guntur Mandi", "Common",
                "08/10/2026", 2000.0, 2400.0, 2200.0, "INR", "₹/quintal",
                "AGMARKNET Archive", "https://agmarknet.gov.in", "RECENT_OBSERVATION"
        );
        MarketObservationDto invalidFutureDate = new MarketObservationDto(
                "id4", "Rice", "AP", "Guntur", "Guntur Mandi", "Common",
                "15/10/2099", 2000.0, 2400.0, 2200.0, "INR", "₹/quintal",
                "AGMARKNET Archive", "https://agmarknet.gov.in/SearchCrosstab.aspx?Tx_Commodity=Rice&Tx_State=AP", "INVALID_FUTURE_DATE"
        );

        assertTrue(provider.hasVerifiedProvenance(valid));
        assertFalse(provider.hasVerifiedProvenance(invalidMissingUrl), "Missing URL must be rejected");
        assertFalse(provider.hasVerifiedProvenance(invalidGenericUrl), "Generic homepage URL without specific query must be rejected");
        assertFalse(provider.hasVerifiedProvenance(invalidFutureDate), "Future dated observations must be rejected");
    }

    @Test
    @DisplayName("AgmarknetMarketDataProvider calculateFreshnessStatus should categorize recent, historical, live, and future dates")
    void provider_CalculateFreshnessStatus_Threshold() {
        String recentStatus = AgmarknetMarketDataProvider.calculateFreshnessStatus("08/10/2026", false);
        String staleStatus = AgmarknetMarketDataProvider.calculateFreshnessStatus("01/01/2025", false);
        String liveStatus = AgmarknetMarketDataProvider.calculateFreshnessStatus("08/10/2026", true);
        String futureStatus = AgmarknetMarketDataProvider.calculateFreshnessStatus("15/10/2099", false);

        assertEquals("RECENT_OBSERVATION", recentStatus);
        assertEquals("HISTORICAL_BENCHMARK", staleStatus);
        assertEquals("LIVE", liveStatus);
        assertEquals("INVALID_FUTURE_DATE", futureStatus);
    }

    @Test
    @DisplayName("Live API failure with verified fallback records should return verified fallback observations")
    void liveProviderFailure_WithVerifiedFallback_ReturnsFallback() {
        MarketObservationDto verifiedFallback = new MarketObservationDto(
                "ap_guntur_rice", "Rice", "Andhra Pradesh", "Guntur", "Guntur Mandi", "Common",
                "08/10/2026", 2200.0, 2450.0, 2350.0, "INR", "₹/quintal",
                "AGMARKNET Archive", "https://agmarknet.gov.in/SearchCrosstab.aspx?Tx_Commodity=Rice&Tx_State=AP", "RECENT_OBSERVATION"
        );

        when(marketDataProvider.getSupportedCrops()).thenReturn(List.of("Rice"));
        when(marketDataProvider.fetchPrices("Rice", "Andhra Pradesh", null, null))
                .thenReturn(List.of(verifiedFallback));

        MarketResponseDto response = marketIntelligenceService.getMarketPrices("Rice", "Andhra Pradesh", null, null);

        assertNotNull(response);
        assertEquals(1, response.getTotalRecords());
        assertEquals("Guntur Mandi", response.getObservations().get(0).getMarketName());
        assertEquals("RECENT_OBSERVATION", response.getObservations().get(0).getFreshnessStatus());
    }

    @Test
    @DisplayName("Live provider failure with no fallback should return transparent empty state")
    void liveProviderFailure_NoFallback_TransparentEmptyState() {
        when(marketDataProvider.getSupportedCrops()).thenReturn(List.of("Groundnut"));
        when(marketDataProvider.fetchPrices("Groundnut", "Andhra Pradesh", null, null))
                .thenReturn(Collections.emptyList());

        MarketResponseDto response = marketIntelligenceService.getMarketPrices("Groundnut", "Andhra Pradesh", null, null);

        assertNotNull(response);
        assertEquals(0, response.getTotalRecords());
        assertTrue(response.getObservations().isEmpty());
    }
}
