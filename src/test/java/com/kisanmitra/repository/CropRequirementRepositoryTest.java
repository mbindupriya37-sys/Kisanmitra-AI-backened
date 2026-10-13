package com.kisanmitra.repository;

import com.kisanmitra.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CropRequirementRepositoryTest {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private CropRequirementRepository cropRequirementRepository;

    @Test
    public void testCropSaveAndUniqueness() {
        Crop crop1 = new Crop("Test Rice 7G", "Oryza sativa", "Staple food crop");
        Crop saved = cropRepository.save(crop1);
        assertNotNull(saved.getId());

        Optional<Crop> found = cropRepository.findByNameIgnoreCase("test rice 7g");
        assertTrue(found.isPresent());
        assertEquals("Test Rice 7G", found.get().getName());

        Crop cropDuplicate = new Crop("Test Rice 7G", "Oryza sativa", "Duplicate crop");
        assertThrows(DataIntegrityViolationException.class, () -> {
            cropRepository.saveAndFlush(cropDuplicate);
        });
    }

    @Test
    public void testCropRequirementSaveAndRetrieval() {
        Crop crop = cropRepository.save(new Crop("Test Wheat 7G", "Triticum aestivum", "Cereal grain"));

        CropRequirement requirement = new CropRequirement(crop);
        requirement.setMinSoilPh(6.0);
        requirement.setMaxSoilPh(7.5);
        requirement.setSuitableSoilTypes(Set.of(SoilType.ALLUVIAL, SoilType.LOAMY, SoilType.BLACK));
        requirement.setSuitableSeasons(Set.of(Season.RABI));
        requirement.setMinRainfall(RainfallLevel.LOW);
        requirement.setMaxRainfall(RainfallLevel.MODERATE);
        requirement.setMinTemperatureRange(TemperatureRange.COOL);
        requirement.setMaxTemperatureRange(TemperatureRange.MODERATE);
        requirement.setMinWaterAvailability(WaterAvailability.MODERATE);
        requirement.setMaxWaterAvailability(WaterAvailability.HIGH);
        requirement.setIrrigationRequired(true);
        requirement.setRiskLevel(CropRiskLevel.LOW);
        requirement.setWaterRequirementDescription("Requires 4-6 irrigations");

        requirement.setSourceName("ICAR-IIWBR");
        requirement.setSourceUrl("https://iiwbr.icar.gov.in");
        requirement.setSourceUpdatedDate(java.time.LocalDate.of(2024, 2, 10));

        CropRequirement savedReq = cropRequirementRepository.save(requirement);
        assertNotNull(savedReq.getId());

        Optional<CropRequirement> foundById = cropRequirementRepository.findByCropId(crop.getId());
        assertTrue(foundById.isPresent());
        assertEquals(3, foundById.get().getSuitableSoilTypes().size());
        assertTrue(foundById.get().getSuitableSoilTypes().contains(SoilType.ALLUVIAL));
        assertTrue(foundById.get().getSuitableSoilTypes().contains(SoilType.LOAMY));
        assertTrue(foundById.get().getSuitableSoilTypes().contains(SoilType.BLACK));

        assertTrue(foundById.get().getSuitableSeasons().contains(Season.RABI));
        assertEquals(CropRiskLevel.LOW, foundById.get().getRiskLevel());
        assertEquals("ICAR-IIWBR", foundById.get().getSourceName());
        assertEquals("https://iiwbr.icar.gov.in", foundById.get().getSourceUrl());
        assertEquals(java.time.LocalDate.of(2024, 2, 10), foundById.get().getSourceUpdatedDate());

        Optional<CropRequirement> foundByName = cropRequirementRepository.findByCropNameIgnoreCase("test wheat 7g");
        assertTrue(foundByName.isPresent());
        assertEquals(crop.getId(), foundByName.get().getCrop().getId());
    }

    @Test
    public void testCropRequirementCannotExistWithoutCrop() {
        CropRequirement requirement = new CropRequirement(null);
        requirement.setSourceName("ICAR");
        requirement.setSourceUrl("https://icar.org.in");

        assertThrows(Exception.class, () -> {
            cropRequirementRepository.saveAndFlush(requirement);
        });
    }
}
