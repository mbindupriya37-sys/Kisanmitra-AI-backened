package com.kisanmitra.service;

import com.kisanmitra.dto.CropRecommendationResultDto;
import com.kisanmitra.dto.FactorResultDto;
import com.kisanmitra.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CropRecommendationEngineTest {

    private CropRecommendationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new CropRecommendationEngine();
    }

    private CropRecommendationRequest createSampleRequest() {
        CropRecommendationRequest req = new CropRecommendationRequest();
        req.setId(100L);
        req.setState("Andhra Pradesh");
        req.setDistrict("Guntur");
        req.setSoilType(SoilType.BLACK);
        req.setSoilPh(6.8);
        req.setSoilPhUnknown(false);
        req.setWaterAvailability(WaterAvailability.HIGH);
        req.setIrrigation(IrrigationType.YES);
        req.setExpectedRainfall(RainfallLevel.HIGH);
        req.setSeason(Season.KHARIF);
        req.setTemperatureRange(TemperatureRange.WARM);
        req.setFarmSize(3.5);
        req.setPriorities(Set.of("Lower crop risk", "Sustainable farming"));
        req.setStatus(RecommendationStatus.PENDING);
        return req;
    }

    private CropRequirement createSampleRiceRequirement() {
        Crop crop = new Crop("Rice", "Oryza sativa", "Staple cereal crop");
        crop.setId(1L);

        CropRequirement req = new CropRequirement(crop);
        req.setId(10L);
        req.setMinSoilPh(5.5);
        req.setMaxSoilPh(7.5);
        req.setSuitableSoilTypes(Set.of(SoilType.ALLUVIAL, SoilType.BLACK, SoilType.CLAY));
        req.setSuitableSeasons(Set.of(Season.KHARIF, Season.ZAID));
        req.setMinRainfall(RainfallLevel.HIGH);
        req.setMaxRainfall(RainfallLevel.VERY_HIGH);
        req.setMinTemperatureRange(TemperatureRange.WARM);
        req.setMaxTemperatureRange(TemperatureRange.HOT);
        req.setMinWaterAvailability(WaterAvailability.HIGH);
        req.setMaxWaterAvailability(WaterAvailability.VERY_HIGH);
        req.setIrrigationRequired(true);
        req.setRiskLevel(CropRiskLevel.LOW);
        req.setWaterRequirementDescription("High water demand");
        req.setGrowingPeriodDescription("120 to 140 days");
        req.setSustainabilityNotes("AWD water management recommended");
        req.setSourceName("ICAR-IIRR");
        req.setSourceUrl("https://www.icar-iirr.org");
        req.setSourceUpdatedDate(LocalDate.of(2024, 1, 15));
        return req;
    }

    @Test
    @DisplayName("1. All factors evaluated -> normal weighted score (>= 90)")
    void testExcellentMatchingCropScore() {
        CropRecommendationRequest request = createSampleRequest();
        CropRequirement requirement = createSampleRiceRequirement();

        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        assertNotNull(result);
        assertTrue(result.getOverallScore() >= 90.0, "Matching crop should score >= 90, got: " + result.getOverallScore());
        assertEquals("EXCELLENT", result.getSuitability());
    }

    @Test
    @DisplayName("2. Soil mismatch lowers score")
    void testSoilMismatchLowersScore() {
        CropRecommendationRequest request = createSampleRequest();
        request.setSoilType(SoilType.SANDY); // Rice requires Alluvial/Black/Clay

        CropRequirement requirement = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        FactorResultDto soilFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Soil Type"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.MISMATCH, soilFactor.getStatus());
        assertEquals(0.0, soilFactor.getScore());
        assertTrue(result.getOverallScore() < 90.0);
    }

    @Test
    @DisplayName("3. Soil pH mismatch lowers score")
    void testSoilPhMismatchLowersScore() {
        CropRecommendationRequest request = createSampleRequest();
        request.setSoilPh(3.5); // Highly acidic, far outside 5.5 - 7.5

        CropRequirement requirement = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        FactorResultDto phFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Soil pH"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.MISMATCH, phFactor.getStatus());
        assertEquals(0.0, phFactor.getScore());
    }

    @Test
    @DisplayName("4. Water mismatch lowers score")
    void testWaterMismatchLowersScore() {
        CropRecommendationRequest request = createSampleRequest();
        request.setWaterAvailability(WaterAvailability.VERY_LOW);

        CropRequirement requirement = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        FactorResultDto waterFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Water Availability"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.MISMATCH, waterFactor.getStatus());
        assertEquals(0.0, waterFactor.getScore());
    }

    @Test
    @DisplayName("5. Rainfall mismatch lowers score")
    void testRainfallMismatchLowersScore() {
        CropRecommendationRequest request = createSampleRequest();
        request.setExpectedRainfall(RainfallLevel.VERY_LOW);

        CropRequirement requirement = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        FactorResultDto rainfallFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Expected Rainfall"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.MISMATCH, rainfallFactor.getStatus());
        assertEquals(0.0, rainfallFactor.getScore());
    }

    @Test
    @DisplayName("6. Season mismatch lowers score")
    void testSeasonMismatchLowersScore() {
        CropRecommendationRequest request = createSampleRequest();
        request.setSeason(Season.RABI); // Rice is Kharif/Zaid

        CropRequirement requirement = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        FactorResultDto seasonFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Season"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.MISMATCH, seasonFactor.getStatus());
        assertEquals(0.0, seasonFactor.getScore());
    }

    @Test
    @DisplayName("7. Temperature mismatch lowers score")
    void testTemperatureMismatchLowersScore() {
        CropRecommendationRequest request = createSampleRequest();
        request.setTemperatureRange(TemperatureRange.COOL); // Rice is WARM/HOT

        CropRequirement requirement = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        FactorResultDto tempFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Temperature Range"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.MISMATCH, tempFactor.getStatus());
        assertEquals(0.0, tempFactor.getScore());
    }

    @Test
    @DisplayName("8. Irrigation mismatch is handled correctly")
    void testIrrigationMismatchHandled() {
        CropRecommendationRequest request = createSampleRequest();
        request.setIrrigation(IrrigationType.NO); // Rice requires irrigation

        CropRequirement requirement = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        FactorResultDto irrFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Irrigation Availability"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.MISMATCH, irrFactor.getStatus());
        assertEquals(0.0, irrFactor.getScore());
    }

    @Test
    @DisplayName("9. Risk level is reflected correctly")
    void testRiskLevelReflection() {
        CropRequirement requirement = createSampleRiceRequirement();
        requirement.setRiskLevel(CropRiskLevel.HIGH);

        CropRecommendationRequest request = createSampleRequest();
        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        assertEquals(CropRiskLevel.HIGH, result.getRiskLevel());
        FactorResultDto riskFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Crop Risk Level"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.PARTIAL, riskFactor.getStatus());
        assertEquals(4.0, riskFactor.getScore());
    }

    @Test
    @DisplayName("10. One factor NOT_EVALUATED -> score normalized over remaining evaluated weights")
    void testOneFactorNotEvaluatedNormalizedScore() {
        CropRequirement req = createSampleRiceRequirement();
        req.setMinSoilPh(null);
        req.setMaxSoilPh(null);

        CropRecommendationRequest request = createSampleRequest();
        request.setPriorities(Set.of()); // No priority bonus

        CropRecommendationResultDto result = engine.evaluateCrop(request, req);

        FactorResultDto phFactor = result.getFactors().stream()
                .filter(f -> f.getFactorName().equalsIgnoreCase("Soil pH"))
                .findFirst().orElseThrow();

        assertEquals(FactorStatus.NOT_EVALUATED, phFactor.getStatus());
        assertEquals(0.0, phFactor.getScore()); // Contributes 0 raw score
        assertEquals(100.0, result.getOverallScore(), "Score should be 100.0 normalized over evaluated 85 weight sum");
        assertTrue(result.getExplanation().contains("Some suitability factors could not be evaluated"));
    }

    @Test
    @DisplayName("11. Multiple NOT_EVALUATED factors -> no free points, normalized over evaluated weights")
    void testMultipleFactorsNotEvaluatedNoFreePoints() {
        CropRequirement req = createSampleRiceRequirement();
        req.setMinRainfall(null);
        req.setMaxRainfall(null);
        req.setMinTemperatureRange(null);
        req.setMaxTemperatureRange(null);

        CropRecommendationRequest request = createSampleRequest();
        request.setSoilType(SoilType.SANDY); // Mismatch soil (0/15)
        request.setPriorities(Set.of());

        CropRecommendationResultDto result = engine.evaluateCrop(request, req);

        // Evaluated weights = 15(pH)+15(water)+15(season)+5(irr)+10(risk) = 60. Evaluated scores = 15+15+15+5+10 = 60 (soil is 0).
        // Score = (60 / 75) * 100 = 80.0
        assertEquals(80.0, result.getOverallScore());
        assertTrue(result.getExplanation().contains("Some suitability factors could not be evaluated"));
    }

    @Test
    @DisplayName("12. All factors NOT_EVALUATED -> safely returns 0.0 score")
    void testAllFactorsNotEvaluatedSafelyReturnsZero() {
        Crop crop = new Crop("Empty Crop", "Unknown", "No requirement data");
        CropRequirement emptyReq = new CropRequirement(crop);

        CropRecommendationRequest request = new CropRecommendationRequest(); // All null
        request.setPriorities(Set.of());

        CropRecommendationResultDto result = engine.evaluateCrop(request, emptyReq);

        assertEquals(0.0, result.getOverallScore(), "Empty requirements must yield 0.0 score");
        assertEquals("POOR", result.getSuitability());
    }

    @Test
    @DisplayName("13. Scores remain between 0 and 100")
    void testScoresBounded() {
        CropRecommendationRequest request = createSampleRequest();
        request.setPriorities(Set.of("Lower water requirement", "Lower crop risk", "Sustainable farming", "Better climate suitability", "Shorter growing period"));

        CropRequirement req = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, req);

        assertTrue(result.getOverallScore() >= 0.0 && result.getOverallScore() <= 100.0);
    }

    @Test
    @DisplayName("14. Priority adjustment cannot exceed 100.0")
    void testPriorityAdjustmentCannotExceed100() {
        CropRecommendationRequest request = createSampleRequest();
        request.setPriorities(Set.of("Lower water requirement", "Lower crop risk", "Sustainable farming", "Better climate suitability", "Shorter growing period"));

        CropRequirement req = createSampleRiceRequirement();
        CropRecommendationResultDto result = engine.evaluateCrop(request, req);

        assertEquals(100.0, result.getOverallScore(), "Score with priority bonus cannot exceed 100.0");
    }

    @Test
    @DisplayName("15. Results are sorted descending")
    void testResultsSortedDescending() {
        CropRecommendationRequest request = createSampleRequest();

        CropRequirement riceReq = createSampleRiceRequirement();

        Crop wheatCrop = new Crop("Wheat", "Triticum aestivum", "Rabi crop");
        wheatCrop.setId(2L);
        CropRequirement wheatReq = new CropRequirement(wheatCrop);
        wheatReq.setSuitableSeasons(Set.of(Season.RABI)); // Mismatch for Kharif request

        List<CropRecommendationResultDto> results = engine.evaluateAllCrops(request, List.of(wheatReq, riceReq));

        assertEquals(2, results.size());
        assertEquals("Rice", results.get(0).getCrop().getName());
        assertEquals("Wheat", results.get(1).getCrop().getName());
        assertTrue(results.get(0).getOverallScore() >= results.get(1).getOverallScore());
    }

    @Test
    @DisplayName("16. Farmer priorities are handled correctly")
    void testFarmerPrioritiesHandled() {
        CropRecommendationRequest reqWithPriority = createSampleRequest();
        reqWithPriority.setSoilPh(8.0); // Partial match pH (7.5 pts instead of 15.0 pts)
        reqWithPriority.setPriorities(Set.of("Lower crop risk", "Sustainable farming"));

        CropRecommendationRequest reqWithoutPriority = createSampleRequest();
        reqWithoutPriority.setSoilPh(8.0);
        reqWithoutPriority.setPriorities(Set.of());

        CropRequirement requirement = createSampleRiceRequirement();

        CropRecommendationResultDto res1 = engine.evaluateCrop(reqWithPriority, requirement);
        CropRecommendationResultDto res2 = engine.evaluateCrop(reqWithoutPriority, requirement);

        assertTrue(res1.getOverallScore() > res2.getOverallScore(),
                "Priority boost should increase overall score. Got withPriority=" + res1.getOverallScore() + ", withoutPriority=" + res2.getOverallScore());
    }

    @Test
    @DisplayName("17. Market opportunity priority awards zero points")
    void testMarketOpportunityNoFabricatedPoints() {
        CropRecommendationRequest req1 = createSampleRequest();
        req1.setPriorities(Set.of());

        CropRecommendationRequest req2 = createSampleRequest();
        req2.setPriorities(Set.of("Market opportunity"));

        CropRequirement requirement = createSampleRiceRequirement();

        CropRecommendationResultDto res1 = engine.evaluateCrop(req1, requirement);
        CropRecommendationResultDto res2 = engine.evaluateCrop(req2, requirement);

        assertEquals(res1.getOverallScore(), res2.getOverallScore(), "Market opportunity should not award points");
    }

    @Test
    @DisplayName("18. Source attribution is returned")
    void testSourceAttributionReturned() {
        CropRecommendationRequest request = createSampleRequest();
        CropRequirement requirement = createSampleRiceRequirement();

        CropRecommendationResultDto result = engine.evaluateCrop(request, requirement);

        assertNotNull(result.getSource());
        assertEquals("ICAR-IIRR", result.getSource().getName());
        assertEquals("https://www.icar-iirr.org", result.getSource().getUrl());
        assertEquals(LocalDate.of(2024, 1, 15), result.getSource().getUpdatedDate());
    }
}
