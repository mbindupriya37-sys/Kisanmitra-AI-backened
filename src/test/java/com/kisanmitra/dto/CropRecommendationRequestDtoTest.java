package com.kisanmitra.dto;

import com.kisanmitra.entity.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CropRecommendationRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CropRecommendationRequestDto createValidDto() {
        CropRecommendationRequestDto dto = new CropRecommendationRequestDto();
        dto.setState("Maharashtra");
        dto.setDistrict("Nashik");
        dto.setVillage("Rampur");
        dto.setSoilType(SoilType.BLACK);
        dto.setSoilPh(6.5);
        dto.setSoilPhUnknown(false);
        dto.setWaterAvailability(WaterAvailability.MODERATE);
        dto.setIrrigation(IrrigationType.YES);
        dto.setExpectedRainfall(RainfallLevel.MODERATE);
        dto.setSeason(Season.KHARIF);
        dto.setTemperatureRange(TemperatureRange.WARM);
        dto.setFarmSize(2.5);
        dto.setPriorities(Set.of("Lower water requirement", "Market opportunity"));
        return dto;
    }

    @Test
    @DisplayName("Valid DTO with known soil pH 6.5 passes validation")
    void testValidDtoWithKnownSoilPh() {
        CropRecommendationRequestDto dto = createValidDto();
        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid DTO should have zero violations");
    }

    @Test
    @DisplayName("Valid DTO with unknown soil pH (null pH) passes validation")
    void testValidDtoWithUnknownSoilPh() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setSoilPhUnknown(true);
        dto.setSoilPh(null);

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Unknown soil pH with null value should be valid");
    }

    @Test
    @DisplayName("Invalid DTO when soilPhUnknown=false but soilPh is null")
    void testInvalidNullSoilPhWhenNotUnknown() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setSoilPhUnknown(false);
        dto.setSoilPh(null);

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Null soil pH when not unknown should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("soilPh")));
    }

    @Test
    @DisplayName("Invalid DTO when soilPh is below 4.0")
    void testInvalidTooLowSoilPh() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setSoilPhUnknown(false);
        dto.setSoilPh(3.5);

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Soil pH 3.5 should fail validation (< 4.0)");
    }

    @Test
    @DisplayName("Invalid DTO when soilPh is above 9.5")
    void testInvalidTooHighSoilPh() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setSoilPhUnknown(false);
        dto.setSoilPh(10.0);

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Soil pH 10.0 should fail validation (> 9.5)");
    }

    @Test
    @DisplayName("Invalid DTO when farmSize is 0")
    void testInvalidFarmSizeZero() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setFarmSize(0.0);

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Farm size 0.0 should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("farmSize")));
    }

    @Test
    @DisplayName("Invalid DTO when farmSize is negative")
    void testInvalidFarmSizeNegative() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setFarmSize(-2.0);

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Farm size -2.0 should fail validation");
    }

    @Test
    @DisplayName("Invalid DTO when state is blank")
    void testInvalidBlankState() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setState("   ");

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Blank state should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Invalid DTO when district is blank")
    void testInvalidBlankDistrict() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setDistrict("");

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Blank district should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("district")));
    }

    @Test
    @DisplayName("Invalid DTO when season is null")
    void testInvalidNullSeason() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setSeason(null);

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Null season should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("season")));
    }

    @Test
    @DisplayName("Invalid DTO when state exceeds 100 characters")
    void testStateExceedingMaxLengthFailsValidation() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setState("A".repeat(101));

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Invalid DTO when district exceeds 100 characters")
    void testDistrictExceedingMaxLengthFailsValidation() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setDistrict("D".repeat(101));

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("district")));
    }

    @Test
    @DisplayName("Invalid DTO when village exceeds 150 characters")
    void testVillageExceedingMaxLengthFailsValidation() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setVillage("V".repeat(151));

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("village")));
    }

    @Test
    @DisplayName("Locality field synchronizes with village and passes validation")
    void testLocalityFieldSyncsWithVillageAndPasses() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setVillage(null);
        dto.setLocality("Tenali Agricultural Mandal");

        assertEquals("Tenali Agricultural Mandal", dto.getVillage());
        assertEquals("Tenali Agricultural Mandal", dto.getLocality());

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Setting locality should populate village and pass validation");
    }

    @Test
    @DisplayName("Optional village/locality can be null or empty")
    void testNullVillageAndLocalityIsValid() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setVillage(null);
        dto.setLocality(null);

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Optional village can be null without validation error");
    }

    @Test
    @DisplayName("Valid 6-digit Indian PIN code passes validation")
    void testValidPinCodePasses() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setPincode("522001");

        Set<ConstraintViolation<CropRecommendationRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid PIN code should pass validation");
    }

    @Test
    @DisplayName("Null or empty PIN code is allowed as it is optional")
    void testNullOrEmptyPinCodeAllowed() {
        CropRecommendationRequestDto dto = createValidDto();
        dto.setPincode(null);
        assertTrue(validator.validate(dto).isEmpty());

        dto.setPincode("");
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("Invalid PIN codes fail validation")
    void testInvalidPinCodesFailValidation() {
        CropRecommendationRequestDto dto = createValidDto();

        // Starts with 0
        dto.setPincode("012345");
        assertFalse(validator.validate(dto).isEmpty(), "PIN starting with 0 should fail");

        // Non-numeric
        dto.setPincode("52200A");
        assertFalse(validator.validate(dto).isEmpty(), "PIN with letter should fail");

        // Too short
        dto.setPincode("52200");
        assertFalse(validator.validate(dto).isEmpty(), "5-digit PIN should fail");

        // Too long
        dto.setPincode("5220011");
        assertFalse(validator.validate(dto).isEmpty(), "7-digit PIN should fail");
    }
}
