package com.kisanmitra.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisanmitra.dto.CropSummaryDto;
import com.kisanmitra.dto.FarmerCropIntentionResponseDto;
import com.kisanmitra.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JpaRelationshipAuditTest {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private CropRequirementRepository cropRequirementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerCropIntentionRepository farmerCropIntentionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("1. @OneToOne: Crop <-> CropRequirement bidirectional mapping and foreign-key uniqueness")
    void testCropAndCropRequirementOneToOneMapping() {
        Crop crop = new Crop("Audit Test Millet", "Panicum miliaceum", "Nutritious minor millet");
        Crop savedCrop = cropRepository.saveAndFlush(crop);

        CropRequirement requirement = new CropRequirement(savedCrop);
        requirement.setMinSoilPh(5.5);
        requirement.setMaxSoilPh(7.0);
        requirement.setSuitableSoilTypes(Set.of(SoilType.RED, SoilType.LOAMY));
        requirement.setSuitableSeasons(Set.of(Season.KHARIF));
        requirement.setMinRainfall(RainfallLevel.LOW);
        requirement.setMaxRainfall(RainfallLevel.MODERATE);
        requirement.setMinTemperatureRange(TemperatureRange.WARM);
        requirement.setMaxTemperatureRange(TemperatureRange.HOT);
        requirement.setMinWaterAvailability(WaterAvailability.LOW);
        requirement.setMaxWaterAvailability(WaterAvailability.MODERATE);
        requirement.setRiskLevel(CropRiskLevel.LOW);
        requirement.setSourceName("ICAR-IIMR");
        requirement.setSourceUrl("https://millets.res.in");

        CropRequirement savedReq = cropRequirementRepository.saveAndFlush(requirement);

        // Verify owning side (@JoinColumn on CropRequirement)
        assertThat(savedReq.getCrop()).isNotNull();
        assertThat(savedReq.getCrop().getId()).isEqualTo(savedCrop.getId());

        // Verify inverse side navigation from Crop
        savedCrop.setCropRequirement(savedReq);
        assertThat(savedCrop.getCropRequirement()).isNotNull();
        assertThat(savedCrop.getCropRequirement().getId()).isEqualTo(savedReq.getId());

        // Verify database-level 1:1 uniqueness (cannot associate duplicate CropRequirement to same crop_id)
        CropRequirement duplicateReq = new CropRequirement(savedCrop);
        duplicateReq.setSourceName("Duplicate Agency");
        duplicateReq.setSourceUrl("https://example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            cropRequirementRepository.saveAndFlush(duplicateReq);
        });
    }

    @Test
    @DisplayName("2. Many-to-Many via Associative Entity: FarmerCropIntention preserves domain payload attributes")
    void testFarmerCropIntentionAssociativeEntityPattern() {
        User farmer = new User();
        farmer.setFullName("Venkat Rao");
        farmer.setEmail("venkat.associative@kisanmitra.in");
        farmer.setPhone("9876543299");
        farmer.setPassword("$2a$10$encryptedPasswordHash");
        farmer.setRole(Role.FARMER);
        User savedFarmer = userRepository.saveAndFlush(farmer);

        Crop crop = cropRepository.findAll().stream().findFirst().orElseThrow();

        // Associative entity carrying crucial domain attributes: state, district, season, acreage
        FarmerCropIntention intention = new FarmerCropIntention(
                savedFarmer,
                "Andhra Pradesh",
                "Guntur",
                crop,
                Season.KHARIF,
                5.0
        );
        FarmerCropIntention savedIntention = farmerCropIntentionRepository.saveAndFlush(intention);

        assertThat(savedIntention.getId()).isNotNull();
        assertThat(savedIntention.getFarmer().getId()).isEqualTo(savedFarmer.getId());
        assertThat(savedIntention.getCrop().getId()).isEqualTo(crop.getId());
        assertThat(savedIntention.getState()).isEqualTo("Andhra Pradesh");
        assertThat(savedIntention.getDistrict()).isEqualTo("Guntur");
        assertThat(savedIntention.getIntendedSeason()).isEqualTo(Season.KHARIF);
        assertThat(savedIntention.getFarmSize()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("3. DTO Decoupling: Entities are projected to DTOs preventing circular JSON serialization")
    void testDtoProjectionDecoupling() throws Exception {
        Crop crop = new Crop("JSON Test Barley", "Hordeum vulgare", "Cereal crop");
        crop.setId(999L);

        CropRequirement requirement = new CropRequirement(crop);
        requirement.setId(888L);
        crop.setCropRequirement(requirement);

        // Verify CropSummaryDto projection does not serialize CropRequirement
        CropSummaryDto summaryDto = CropSummaryDto.fromEntity(crop);
        String json = objectMapper.writeValueAsString(summaryDto);

        assertThat(json).contains("\"name\":\"JSON Test Barley\"");
        assertThat(json).doesNotContain("cropRequirement");

        // Verify Crop entity itself handles circular serialization gracefully via @JsonIgnore
        String cropJson = objectMapper.writeValueAsString(crop);
        assertThat(cropJson).contains("\"name\":\"JSON Test Barley\"");
        assertThat(cropJson).doesNotContain("cropRequirement");
    }

    @Test
    @DisplayName("4. DTO Decoupling: FarmerCropIntentionResponseDto projects summary without leaking user password")
    void testFarmerCropIntentionDtoSafety() throws Exception {
        User farmer = new User();
        farmer.setId(101L);
        farmer.setFullName("Private Farmer");
        farmer.setEmail("private@kisanmitra.in");
        farmer.setPassword("SuperSecretHash123");
        farmer.setRole(Role.FARMER);

        Crop crop = new Crop("Safe Cotton", "Gossypium hirsutum", "Fiber crop");
        crop.setId(202L);

        FarmerCropIntention intention = new FarmerCropIntention(
                farmer, "Maharashtra", "Nagpur", crop, Season.KHARIF, 10.0
        );
        intention.setId(303L);

        FarmerCropIntentionResponseDto responseDto = FarmerCropIntentionResponseDto.fromEntity(intention);
        String json = objectMapper.writeValueAsString(responseDto);

        assertThat(json).contains("\"state\":\"Maharashtra\"");
        assertThat(json).contains("\"district\":\"Nagpur\"");
        assertThat(json).contains("\"farmSize\":10.0");
        assertThat(json).contains("\"name\":\"Safe Cotton\"");
        // Sensitive user credentials must NEVER appear in the response payload
        assertThat(json).doesNotContain("SuperSecretHash123");
        assertThat(json).doesNotContain("private@kisanmitra.in");
    }
}
