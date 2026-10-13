package com.kisanmitra.config;

import com.kisanmitra.entity.*;
import com.kisanmitra.repository.CropRepository;
import com.kisanmitra.repository.CropRequirementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CropKnowledgeSeederTest {

    @Autowired
    private CropKnowledgeSeeder cropKnowledgeSeeder;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private CropRequirementRepository cropRequirementRepository;

    @Test
    public void testSeederIdempotencyAndNoDuplicates() throws Exception {
        // Run seeder first time
        cropKnowledgeSeeder.run();
        long countAfterFirstRun = cropRepository.count();
        assertTrue(countAfterFirstRun >= 7, "Seeder should insert at least 7 verified crops");

        // Run seeder second time
        cropKnowledgeSeeder.run();
        long countAfterSecondRun = cropRepository.count();
        assertEquals(countAfterFirstRun, countAfterSecondRun, "Running seeder twice must be idempotent and not create duplicate crops");
    }

    @Test
    public void testSeederRefusesMissingSourceName() {
        assertThrows(IllegalArgumentException.class, () -> {
            cropKnowledgeSeeder.seedCrop(
                    "Test Crop Invalid Source", "Testus invalidus", "Description",
                    6.0, 7.0, Set.of(SoilType.LOAMY), Set.of(Season.KHARIF),
                    RainfallLevel.MODERATE, RainfallLevel.HIGH,
                    TemperatureRange.WARM, TemperatureRange.HOT,
                    WaterAvailability.MODERATE, WaterAvailability.HIGH,
                    false, CropRiskLevel.LOW, "Water desc", "Period desc", "Sustainability",
                    "", // Empty sourceName
                    "https://icar.gov.in",
                    LocalDate.now()
            );
        });
    }

    @Test
    public void testSeederRefusesMissingOrInvalidSourceUrl() {
        assertThrows(IllegalArgumentException.class, () -> {
            cropKnowledgeSeeder.seedCrop(
                    "Test Crop Invalid URL", "Testus invalidus", "Description",
                    6.0, 7.0, Set.of(SoilType.LOAMY), Set.of(Season.KHARIF),
                    RainfallLevel.MODERATE, RainfallLevel.HIGH,
                    TemperatureRange.WARM, TemperatureRange.HOT,
                    WaterAvailability.MODERATE, WaterAvailability.HIGH,
                    false, CropRiskLevel.LOW, "Water desc", "Period desc", "Sustainability",
                    "ICAR",
                    "invalid-url-without-http", // Invalid sourceUrl
                    LocalDate.now()
            );
        });
    }

    @Test
    public void testSeederDoesNotOverwriteExistingKnowledge() throws Exception {
        // Manually seed custom crop
        String customCropName = "Custom Sorghum Test";
        Crop crop = cropRepository.save(new Crop(customCropName, "Sorghum bicolor", "Manual crop"));
        CropRequirement req = new CropRequirement(crop);
        req.setSourceName("Original Source");
        req.setSourceUrl("https://original-source.gov.in");
        cropRequirementRepository.save(req);

        // Attempt seeding with same crop name
        cropKnowledgeSeeder.seedCrop(
                customCropName, "Sorghum bicolor", "Seeder crop description",
                6.0, 7.5, Set.of(SoilType.LOAMY), Set.of(Season.KHARIF),
                RainfallLevel.LOW, RainfallLevel.MODERATE,
                TemperatureRange.WARM, TemperatureRange.HOT,
                WaterAvailability.LOW, WaterAvailability.MODERATE,
                false, CropRiskLevel.LOW, "Water", "Period", "Notes",
                "New Seeder Source",
                "https://new-seeder-source.gov.in",
                LocalDate.now()
        );

        Optional<CropRequirement> found = cropRequirementRepository.findByCropNameIgnoreCase(customCropName);
        assertTrue(found.isPresent());
        assertEquals("Original Source", found.get().getSourceName(), "Seeder must not overwrite existing crop knowledge");
    }
}
