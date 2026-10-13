package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class CropSupplyDemandService {

    private static final Logger log = LoggerFactory.getLogger(CropSupplyDemandService.class);

    private final CropRepository cropRepository;
    private final CropHistoricalProductionRepository productionRepository;
    private final MspPriceNotificationRepository mspRepository;
    private final PublicBufferStockRepository bufferStockRepository;
    private final PublicProcurementRepository procurementRepository;
    private final MarketDataProvider marketDataProvider;

    @Autowired
    public CropSupplyDemandService(
            CropRepository cropRepository,
            CropHistoricalProductionRepository productionRepository,
            MspPriceNotificationRepository mspRepository,
            PublicBufferStockRepository bufferStockRepository,
            PublicProcurementRepository procurementRepository,
            @Autowired(required = false) MarketDataProvider marketDataProvider) {
        this.cropRepository = cropRepository;
        this.productionRepository = productionRepository;
        this.mspRepository = mspRepository;
        this.bufferStockRepository = bufferStockRepository;
        this.procurementRepository = procurementRepository;
        this.marketDataProvider = marketDataProvider;
    }

    @Transactional(readOnly = true)
    public SupplyDemandOverviewResponseDto getSupplyDemandOverview(String cropName, String state, String district, String season) {
        SupplyDemandOverviewResponseDto response = new SupplyDemandOverviewResponseDto();
        response.setQueryCrop(cropName);
        response.setQueryState(state);
        response.setQueryDistrict(district);
        response.setQuerySeason(season);

        Optional<Crop> cropOpt = cropRepository.findByNameIgnoreCase(cropName);
        Long cropId = cropOpt.map(Crop::getId).orElse(null);

        // 1. District Historical Production Panel
        response.setHistoricalProduction(buildHistoricalProduction(cropId, cropName, state, district));

        // 2. Mandi Wholesale Prices Panel
        response.setMandiPrices(buildMandiPrices(cropName, state, district));

        // 3. Dynamic MSP Notification Panel
        response.setMspNotification(buildMspNotification(cropId, cropName, season));

        // 4. Central Pool Buffer Stocks Panel
        response.setBufferStocks(buildBufferStocks(cropName));

        // 5. Public Foodgrain Procurement Panel
        response.setPublicProcurement(buildProcurement(cropName, state, season));

        // 6. Regional Demand Balance (Immutable INSUFFICIENT_DATA contract)
        response.setRegionalDemandBalance(new DemandStatusDto());

        return response;
    }

    private HistoricalProductionDto buildHistoricalProduction(Long cropId, String cropName, String state, String district) {
        HistoricalProductionDto dto = new HistoricalProductionDto();
        dto.setCropName(cropName);
        dto.setState(state);
        dto.setDistrict(district);
        dto.setObservations(new ArrayList<>());

        if (cropId == null || state == null || district == null) {
            dto.setBoundaryNotice("Please specify valid crop, state, and district to load official production history.");
            return dto;
        }

        List<CropHistoricalProductionObservation> obsList = productionRepository.findActiveByCropAndDistrict(cropId, state, district);

        BigDecimal sumTonnes = BigDecimal.ZERO;
        int validYearsCount = 0;
        String latestVintage = "STANDARD_LGD_2024";
        ProvenanceMetaDto prov = null;

        for (CropHistoricalProductionObservation obs : obsList) {
            HistoricalProductionDto.ProductionObservationItem item = new HistoricalProductionDto.ProductionObservationItem();
            item.setAgriculturalYear(obs.getAgriculturalYear());
            item.setSeason(obs.getSeason());
            item.setAreaHectares(obs.getAreaHectares());
            item.setRawProductionValue(obs.getRawProductionValue());
            item.setRawUnit(obs.getRawUnit());
            item.setProductionTonnes(obs.getProductionTonnes());
            item.setYieldKgPerHa(obs.getYieldKgPerHa());
            item.setConversionFactorApplied(obs.getConversionFactorApplied());
            item.setConversionVerificationRef(obs.getConversionVerificationRef());
            item.setSourceRevision(obs.getSourceRevision());
            item.setDataStatus(obs.getDataStatus());
            dto.getObservations().add(item);

            if (obs.getProductionTonnes() != null && !"NOT_REPORTED".equalsIgnoreCase(obs.getDataStatus())) {
                sumTonnes = sumTonnes.add(obs.getProductionTonnes());
                validYearsCount++;
            }

            latestVintage = obs.getBoundaryVintage();

            if (prov == null) {
                prov = new ProvenanceMetaDto(
                        obs.getSourceOrganization(),
                        obs.getSourceReferenceUrl(),
                        obs.getPublicationDate(),
                        obs.getAgriculturalYear() + " " + obs.getSeason(),
                        "DISTRICT",
                        obs.getRawUnit(),
                        obs.getImportTimestamp(),
                        obs.getDataStatus()
                );
            }
        }

        dto.setBoundaryVintage(latestVintage);
        if ("PRE_2022_BIFURCATION".equalsIgnoreCase(latestVintage)) {
            dto.setBoundaryNotice("Historical observations prior to 2022 reflect the parent district boundary before territorial reorganization.");
        }

        if (validYearsCount > 0) {
            dto.setFiveYearAverageProductionTonnes(sumTonnes.divide(BigDecimal.valueOf(validYearsCount), 2, RoundingMode.HALF_UP));
        }

        dto.setProvenance(prov != null ? prov : new ProvenanceMetaDto(
                "DES_MoAFW",
                "https://desagri.gov.in",
                null,
                "Historical 5-Year Window",
                "DISTRICT",
                "TONNES",
                null,
                "NOT_REPORTED"
        ));

        return dto;
    }

    private MandiPriceObservationDto buildMandiPrices(String crop, String state, String district) {
        MandiPriceObservationDto dto = new MandiPriceObservationDto();
        dto.setObservations(new ArrayList<>());
        dto.setDisclaimer("Daily wholesale prices reported via AGMARKNET. Public daily endpoint reports price observations only; arrival quantities are not reported and cannot be inferred.");

        if (marketDataProvider == null) {
            dto.setAvailabilityStatus("UNCONFIRMED_OR_OFFLINE");
            return dto;
        }

        try {
            List<MarketObservationDto> liveRecords = marketDataProvider.fetchPrices(crop, state, district, null);
            if (liveRecords == null || liveRecords.isEmpty()) {
                dto.setAvailabilityStatus("UNCONFIRMED_OR_OFFLINE");
            } else {
                dto.setAvailabilityStatus("LIVE_CONFIRMED");
                for (MarketObservationDto rec : liveRecords) {
                    MandiPriceObservationDto.MandiPriceRecord item = new MandiPriceObservationDto.MandiPriceRecord();
                    item.setMarketName(rec.getMarketName());
                    item.setDistrict(rec.getDistrict());
                    item.setState(rec.getState());
                    item.setVariety(rec.getVariety());
                    try {
                        item.setArrivalDate(LocalDate.parse(rec.getObservationDate()));
                    } catch (Exception ignored) {}
                    item.setMinPriceInrPerQuintal(rec.getMinPrice() != null ? BigDecimal.valueOf(rec.getMinPrice()) : null);
                    item.setMaxPriceInrPerQuintal(rec.getMaxPrice() != null ? BigDecimal.valueOf(rec.getMaxPrice()) : null);
                    item.setModalPriceInrPerQuintal(rec.getModalPrice() != null ? BigDecimal.valueOf(rec.getModalPrice()) : null);
                    dto.getObservations().add(item);
                }

                MarketObservationDto first = liveRecords.get(0);
                dto.setProvenance(new ProvenanceMetaDto(
                        first.getSourceName() != null ? first.getSourceName() : "AGMARKNET_DMI",
                        first.getSourceUrl(),
                        null,
                        first.getObservationDate(),
                        "MANDI",
                        "INR_PER_QUINTAL",
                        null,
                        "VERIFIED"
                ));
            }
        } catch (Exception e) {
            log.warn("AGMARKNET market data call failed or timed out: {}", e.getMessage());
            dto.setAvailabilityStatus("UNCONFIRMED_OR_OFFLINE");
        }

        return dto;
    }

    private MspNotificationDto buildMspNotification(Long cropId, String cropName, String season) {
        MspNotificationDto dto = new MspNotificationDto();
        dto.setCropName(cropName);

        if (cropId == null) {
            dto.setStatus("UNRESOLVED_MAPPING");
            dto.setStatusExplanation("Crop '" + cropName + "' could not be mapped to central MSP statutory commodity catalog.");
            return dto;
        }

        String seasonType = "KHARIF".equalsIgnoreCase(season) ? "KMS" : "RMS";
        dto.setMarketingSeasonType(seasonType);

        List<MspPriceNotification> mspList = mspRepository.findLatestActiveByCrop(cropId);
        if (mspList.isEmpty()) {
            dto.setStatus("MISSING_NOTIFICATION");
            dto.setStatusExplanation("Official CCEA MSP notification has not yet been notified for " + cropName + " (" + seasonType + "). No hardcoded values are substituted.");
            return dto;
        }

        MspPriceNotification activeMsp = mspList.get(0);
        dto.setStatus("ACTIVE");
        dto.setCommodityName(activeMsp.getCommodityName());
        dto.setCommodityVariety(activeMsp.getCommodityVariety());
        dto.setSeasonYear(activeMsp.getSeasonYear());
        dto.setMspInrPerQuintal(activeMsp.getMspInrPerQuintal());
        dto.setBonusInrPerQuintal(activeMsp.getBonusInrPerQuintal());
        dto.setEffectiveMspInrPerQuintal(activeMsp.getMspInrPerQuintal().add(activeMsp.getBonusInrPerQuintal()));
        dto.setCostA2FlInrPerQuintal(activeMsp.getCostA2FlInrPerQuintal());
        dto.setCostC2InrPerQuintal(activeMsp.getCostC2InrPerQuintal());
        dto.setGazetteNotificationRef(activeMsp.getGazetteNotificationRef());
        dto.setEffectiveDate(activeMsp.getEffectiveDate());

        dto.setProvenance(new ProvenanceMetaDto(
                activeMsp.getSourceOrganization(),
                activeMsp.getSourceReferenceUrl(),
                activeMsp.getPublicationDate(),
                activeMsp.getSeasonYear(),
                "NATIONAL",
                "INR_PER_QUINTAL",
                null,
                "VERIFIED"
        ));

        return dto;
    }

    private BufferStockDto buildBufferStocks(String crop) {
        BufferStockDto dto = new BufferStockDto();
        String commodity = resolveBufferCommodity(crop);
        dto.setCommodity(commodity);

        if (commodity == null) {
            dto.setComparisonStatus("NOT_APPLICABLE");
            dto.setExplanatoryNote("Central pool buffer norms apply specifically to Wheat and Rice under the National Food Security Act (NFSA).");
            return dto;
        }

        List<PublicBufferStockObservation> stocks = bufferStockRepository.findByCommodityOrderByDateDesc(commodity);
        if (stocks.isEmpty()) {
            dto.setComparisonStatus("UNAVAILABLE");
            dto.setExplanatoryNote("No official central pool stock observations on record for " + commodity + ".");
            return dto;
        }

        PublicBufferStockObservation latest = stocks.get(0);
        dto.setObservationDate(latest.getObservationDate());
        dto.setActualCentralPoolLmt(latest.getActualCentralPoolLmt());
        dto.setOperationalNormLmt(latest.getOperationalNormLmt());
        dto.setStrategicReserveNormLmt(latest.getStrategicReserveNormLmt());
        dto.setTotalBufferNormLmt(latest.getTotalBufferNormLmt());
        dto.setNormMilestoneDate(latest.getNormMilestoneDate());
        dto.setComparisonStatus(latest.getComparisonStatus());
        dto.setSurplusDeficitLmt(latest.getSurplusDeficitLmt());
        dto.setExplanatoryNote(latest.getNotes());

        dto.setProvenance(new ProvenanceMetaDto(
                latest.getSourceOrganization(),
                latest.getSourceReferenceUrl(),
                latest.getPublicationDate(),
                latest.getObservationDate().toString(),
                "NATIONAL",
                "LMT",
                null,
                "VERIFIED"
        ));

        return dto;
    }

    private ProcurementObservationDto buildProcurement(String crop, String state, String season) {
        ProcurementObservationDto dto = new ProcurementObservationDto();
        String commodity = resolveBufferCommodity(crop);
        dto.setCommodity(commodity);

        if (commodity == null) {
            return dto;
        }

        List<PublicProcurementObservation> procList = procurementRepository.findLatestByCommodity(commodity);
        if (procList.isEmpty()) {
            return dto;
        }

        PublicProcurementObservation latest = procList.get(0);
        dto.setMarketingSeasonType(latest.getMarketingSeasonType());
        dto.setMarketingSeasonYear(latest.getMarketingSeasonYear());
        dto.setGeographicLevel(latest.getGeographicLevel());
        dto.setStateName(latest.getStateName());
        dto.setReportingAsOfDate(latest.getReportingAsOfDate());
        dto.setProcurementStage(latest.getProcurementStage());
        dto.setProgressiveQuantityLmt(latest.getProgressiveQuantityLmt());
        dto.setTargetProcurementLmt(latest.getTargetProcurementLmt());

        dto.setProvenance(new ProvenanceMetaDto(
                latest.getSourceOrganization(),
                latest.getSourceReferenceUrl(),
                latest.getPublicationDate(),
                latest.getReportingAsOfDate().toString(),
                latest.getGeographicLevel(),
                "LMT",
                null,
                "VERIFIED"
        ));

        return dto;
    }

    private String resolveBufferCommodity(String crop) {
        if (crop == null) return null;
        String upper = crop.trim().toUpperCase();
        if (upper.contains("WHEAT")) return "WHEAT";
        if (upper.contains("RICE") || upper.contains("PADDY")) return "RICE";
        return null;
    }
}
