package com.kisanmitra.config;

import com.kisanmitra.entity.*;
import com.kisanmitra.repository.CropRepository;
import com.kisanmitra.repository.CropRequirementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "kisanmitra.crop-knowledge.seed-enabled", havingValue = "true", matchIfMissing = false)
public class CropKnowledgeSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CropKnowledgeSeeder.class);

    private final CropRepository cropRepository;
    private final CropRequirementRepository cropRequirementRepository;

    public CropKnowledgeSeeder(CropRepository cropRepository, CropRequirementRepository cropRequirementRepository) {
        this.cropRepository = cropRepository;
        this.cropRequirementRepository = cropRequirementRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Initializing KisanMitra AI Crop Knowledge Seeder...");

        seedCrop(
                "Rice",
                "Oryza sativa",
                "Primary cereal grain and staple food crop of India widely cultivated in Kharif season.",
                5.5, 7.5,
                Set.of(SoilType.ALLUVIAL, SoilType.CLAY, SoilType.LOAMY, SoilType.BLACK),
                Set.of(Season.KHARIF, Season.ZAID),
                RainfallLevel.HIGH, RainfallLevel.VERY_HIGH,
                TemperatureRange.WARM, TemperatureRange.HOT,
                WaterAvailability.HIGH, WaterAvailability.VERY_HIGH,
                true,
                CropRiskLevel.MEDIUM,
                "High water requirement (~900-1200mm); needs continuous moist soil condition.",
                "105 to 150 days depending on cultivar.",
                "Alternate Wetting and Drying (AWD) recommended for water conservation.",
                "ICAR - Indian Institute of Rice Research (ICAR-IIRR)",
                "https://www.icar-iirr.org",
                LocalDate.of(2024, 1, 15)
        );

        seedCrop(
                "Wheat",
                "Triticum aestivum",
                "Major rabi cereal crop widely grown in northern and central plains of India.",
                6.0, 7.5,
                Set.of(SoilType.ALLUVIAL, SoilType.LOAMY, SoilType.BLACK),
                Set.of(Season.RABI),
                RainfallLevel.LOW, RainfallLevel.MODERATE,
                TemperatureRange.COOL, TemperatureRange.MODERATE,
                WaterAvailability.MODERATE, WaterAvailability.HIGH,
                true,
                CropRiskLevel.LOW,
                "Moderate water requirement (~450-650mm); critical stages include crown root initiation and flowering.",
                "110 to 140 days.",
                "Zero-tillage and crop residue management improve soil organic carbon.",
                "ICAR - Indian Institute of Wheat and Barley Research (ICAR-IIWBR)",
                "https://iiwbr.icar.gov.in",
                LocalDate.of(2024, 2, 10)
        );

        seedCrop(
                "Maize",
                "Zea mays",
                "Versatile cereal crop used for human food, animal feed, and industrial starch.",
                5.5, 7.5,
                Set.of(SoilType.ALLUVIAL, SoilType.LOAMY, SoilType.BLACK, SoilType.RED),
                Set.of(Season.KHARIF, Season.RABI, Season.ZAID),
                RainfallLevel.MODERATE, RainfallLevel.HIGH,
                TemperatureRange.MODERATE, TemperatureRange.WARM,
                WaterAvailability.MODERATE, WaterAvailability.HIGH,
                false,
                CropRiskLevel.LOW,
                "Moderate water demand (~500-800mm); highly sensitive to waterlogging.",
                "85 to 120 days.",
                "Excellent component for crop rotation with pulses and oilseeds.",
                "ICAR - Indian Institute of Maize Research (ICAR-IIMR)",
                "https://iimr.icar.gov.in",
                LocalDate.of(2024, 1, 20)
        );

        seedCrop(
                "Cotton",
                "Gossypium hirsutum",
                "Major commercial fiber crop grown extensively in black cotton soils.",
                6.0, 8.0,
                Set.of(SoilType.BLACK, SoilType.ALLUVIAL, SoilType.LOAMY),
                Set.of(Season.KHARIF),
                RainfallLevel.MODERATE, RainfallLevel.HIGH,
                TemperatureRange.WARM, TemperatureRange.HOT,
                WaterAvailability.MODERATE, WaterAvailability.HIGH,
                false,
                CropRiskLevel.HIGH,
                "Water requirement ~700-1200mm; sensitive to excess water during boll formation.",
                "150 to 180 days.",
                "Integrated Pest Management (IPM) essential to manage pest resistance.",
                "ICAR - Central Institute for Cotton Research (ICAR-CICR)",
                "https://cicr.icar.gov.in",
                LocalDate.of(2023, 11, 5)
        );

        seedCrop(
                "Chickpea",
                "Cicer arietinum",
                "Premier pulse crop of India rich in protein and atmospheric nitrogen fixing capacity.",
                6.0, 8.0,
                Set.of(SoilType.LOAMY, SoilType.BLACK, SoilType.ALLUVIAL, SoilType.SANDY),
                Set.of(Season.RABI),
                RainfallLevel.VERY_LOW, RainfallLevel.MODERATE,
                TemperatureRange.COOL, TemperatureRange.MODERATE,
                WaterAvailability.LOW, WaterAvailability.MODERATE,
                false,
                CropRiskLevel.LOW,
                "Low water requirement (~250-450mm); thrives in residual soil moisture.",
                "90 to 120 days.",
                "Fixes 30-50 kg/ha atmospheric nitrogen, enhancing soil fertility.",
                "ICAR - Indian Institute of Pulses Research (ICAR-IIPR)",
                "https://iipr.icar.gov.in",
                LocalDate.of(2023, 12, 1)
        );

        seedCrop(
                "Pigeon Pea",
                "Cajanus cajan",
                "Major Kharif pulse crop providing protein and improving subsoil drainage.",
                5.5, 7.5,
                Set.of(SoilType.LOAMY, SoilType.RED, SoilType.BLACK, SoilType.ALLUVIAL),
                Set.of(Season.KHARIF),
                RainfallLevel.LOW, RainfallLevel.MODERATE,
                TemperatureRange.WARM, TemperatureRange.HOT,
                WaterAvailability.LOW, WaterAvailability.MODERATE,
                false,
                CropRiskLevel.MEDIUM,
                "Drought-tolerant crop (~350-600mm); deep taproot system.",
                "150 to 200 days for long-duration cultivars.",
                "Deep roots break hardpans and enrich soil with organic matter.",
                "ICAR - Indian Institute of Pulses Research (ICAR-IIPR)",
                "https://iipr.icar.gov.in",
                LocalDate.of(2023, 12, 1)
        );

        seedCrop(
                "Groundnut",
                "Arachis hypogaea",
                "Leading oilseed crop grown for edible oil and high-protein meal.",
                6.0, 7.5,
                Set.of(SoilType.SANDY, SoilType.LOAMY, SoilType.RED),
                Set.of(Season.KHARIF, Season.ZAID),
                RainfallLevel.LOW, RainfallLevel.MODERATE,
                TemperatureRange.WARM, TemperatureRange.HOT,
                WaterAvailability.LOW, WaterAvailability.MODERATE,
                false,
                CropRiskLevel.MEDIUM,
                "Moderate water demand (~450-600mm); requires well-drained friable soil for pegging.",
                "100 to 135 days.",
                "Improves soil health via biological nitrogen fixation.",
                "ICAR - Directorate of Groundnut Research (ICAR-DGR)",
                "https://dgr.icar.gov.in",
                LocalDate.of(2024, 1, 8)
        );

        logger.info("Crop Knowledge Seeding completed successfully.");
    }

    public void seedCrop(
            String name,
            String scientificName,
            String description,
            Double minSoilPh,
            Double maxSoilPh,
            Set<SoilType> soilTypes,
            Set<Season> seasons,
            RainfallLevel minRainfall,
            RainfallLevel maxRainfall,
            TemperatureRange minTemp,
            TemperatureRange maxTemp,
            WaterAvailability minWater,
            WaterAvailability maxWater,
            Boolean irrigationRequired,
            CropRiskLevel riskLevel,
            String waterDesc,
            String periodDesc,
            String sustainabilityNotes,
            String sourceName,
            String sourceUrl,
            LocalDate sourceDate
    ) {
        // Source Validation Rules
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Crop name cannot be empty");
        }
        if (sourceName == null || sourceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Source name cannot be empty for crop: " + name);
        }
        if (sourceUrl == null || sourceUrl.trim().isEmpty() || (!sourceUrl.startsWith("http://") && !sourceUrl.startsWith("https://"))) {
            throw new IllegalArgumentException("Valid HTTP/HTTPS source URL is required for crop: " + name);
        }

        // Idempotency check: Do not duplicate or overwrite existing crop
        if (cropRepository.existsByNameIgnoreCase(name)) {
            logger.info("Crop '{}' already exists. Skipping seeding.", name);
            return;
        }

        Crop crop = new Crop(name, scientificName, description);
        Crop savedCrop = cropRepository.save(crop);

        CropRequirement req = new CropRequirement(savedCrop);
        req.setMinSoilPh(minSoilPh);
        req.setMaxSoilPh(maxSoilPh);
        req.setSuitableSoilTypes(soilTypes);
        req.setSuitableSeasons(seasons);
        req.setMinRainfall(minRainfall);
        req.setMaxRainfall(maxRainfall);
        req.setMinTemperatureRange(minTemp);
        req.setMaxTemperatureRange(maxTemp);
        req.setMinWaterAvailability(minWater);
        req.setMaxWaterAvailability(maxWater);
        req.setIrrigationRequired(irrigationRequired);
        req.setRiskLevel(riskLevel);
        req.setWaterRequirementDescription(waterDesc);
        req.setGrowingPeriodDescription(periodDesc);
        req.setSustainabilityNotes(sustainabilityNotes);
        req.setSourceName(sourceName);
        req.setSourceUrl(sourceUrl);
        req.setSourceUpdatedDate(sourceDate);

        cropRequirementRepository.save(req);
        logger.info("Seeded crop '{}' with verified source attribution from '{}'", name, sourceName);
    }
}
