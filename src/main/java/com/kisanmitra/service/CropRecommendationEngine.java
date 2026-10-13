package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CropRecommendationEngine {

    public List<CropRecommendationResultDto> evaluateAllCrops(CropRecommendationRequest request,
                                                               List<CropRequirement> activeRequirements) {
        if (request == null || activeRequirements == null) {
            return new ArrayList<>();
        }

        return activeRequirements.stream()
                .map(req -> evaluateCrop(request, req))
                .sorted((r1, r2) -> Double.compare(r2.getOverallScore(), r1.getOverallScore()))
                .collect(Collectors.toList());
    }

    public CropRecommendationResultDto evaluateCrop(CropRecommendationRequest request, CropRequirement req) {
        List<FactorResultDto> factors = new ArrayList<>();

        // 1. Soil Type (15%)
        FactorResultDto soilFactor = evaluateSoilType(request, req);
        factors.add(soilFactor);

        // 2. Soil pH (15%)
        FactorResultDto phFactor = evaluateSoilPh(request, req);
        factors.add(phFactor);

        // 3. Water Availability (15%)
        FactorResultDto waterFactor = evaluateWaterAvailability(request, req);
        factors.add(waterFactor);

        // 4. Expected Rainfall (15%)
        FactorResultDto rainfallFactor = evaluateRainfall(request, req);
        factors.add(rainfallFactor);

        // 5. Season (15%)
        FactorResultDto seasonFactor = evaluateSeason(request, req);
        factors.add(seasonFactor);

        // 6. Temperature Range (10%)
        FactorResultDto tempFactor = evaluateTemperature(request, req);
        factors.add(tempFactor);

        // 7. Irrigation Availability (5%)
        FactorResultDto irrigationFactor = evaluateIrrigation(request, req);
        factors.add(irrigationFactor);

        // 8. Crop Risk Level (10%)
        FactorResultDto riskFactor = evaluateRiskLevel(req);
        factors.add(riskFactor);

        // Sum evaluated weights and scores (excluding NOT_EVALUATED factors)
        double totalEvaluatedWeight = factors.stream()
                .filter(f -> f.getStatus() != FactorStatus.NOT_EVALUATED)
                .mapToDouble(FactorResultDto::getMaxWeight)
                .sum();

        double totalEvaluatedScore = factors.stream()
                .filter(f -> f.getStatus() != FactorStatus.NOT_EVALUATED)
                .mapToDouble(FactorResultDto::getScore)
                .sum();

        // Calculate normalized agronomic score (0 - 100)
        double agronomicScore = (totalEvaluatedWeight > 0.0)
                ? (totalEvaluatedScore / totalEvaluatedWeight) * 100.0
                : 0.0;

        // Priority bonus adjustment applied after agronomic normalization
        double priorityBonus = calculatePriorityBonus(request, req, rainfallFactor, tempFactor);

        double rawTotal = agronomicScore + priorityBonus;
        double overallScore = Math.min(100.0, Math.max(0.0, Math.round(rawTotal * 10.0) / 10.0));

        String suitability = getSuitabilityCategory(overallScore);
        String explanation = generateOverallExplanation(req.getCrop().getName(), suitability, overallScore, factors);

        CropSummaryDto cropSummary = CropSummaryDto.fromEntity(req.getCrop());
        SourceAttributionDto source = new SourceAttributionDto(req.getSourceName(), req.getSourceUrl(), req.getSourceUpdatedDate());

        return new CropRecommendationResultDto(
                cropSummary,
                overallScore,
                suitability,
                req.getRiskLevel(),
                factors,
                explanation,
                req.getWaterRequirementDescription(),
                req.getGrowingPeriodDescription(),
                req.getSustainabilityNotes(),
                source
        );
    }

    private FactorResultDto evaluateSoilType(CropRecommendationRequest request, CropRequirement req) {
        double maxWeight = 15.0;
        Set<SoilType> suitableSoils = req.getSuitableSoilTypes();
        SoilType farmerSoil = request.getSoilType();

        if (suitableSoils == null || suitableSoils.isEmpty()) {
            return new FactorResultDto("Soil Type", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Soil type requirement not specified for this crop.");
        }
        if (farmerSoil == null || farmerSoil == SoilType.UNKNOWN || farmerSoil == SoilType.OTHER) {
            return new FactorResultDto("Soil Type", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Farmer soil type is unknown or general ('" + farmerSoil + "').");
        }

        if (suitableSoils.contains(farmerSoil)) {
            return new FactorResultDto("Soil Type", FactorStatus.MATCH, maxWeight, maxWeight,
                    farmerSoil + " soil is listed as suitable for this crop.");
        } else {
            return new FactorResultDto("Soil Type", FactorStatus.MISMATCH, 0.0, maxWeight,
                    farmerSoil + " soil is not listed among optimal soil types for this crop.");
        }
    }

    private FactorResultDto evaluateSoilPh(CropRecommendationRequest request, CropRequirement req) {
        double maxWeight = 15.0;
        Double minPh = req.getMinSoilPh();
        Double maxPh = req.getMaxSoilPh();

        if (Boolean.TRUE.equals(request.getSoilPhUnknown()) || request.getSoilPh() == null) {
            return new FactorResultDto("Soil pH", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Soil pH was marked as unknown or not provided.");
        }
        if (minPh == null && maxPh == null) {
            return new FactorResultDto("Soil pH", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Optimal soil pH limits are not specified for this crop.");
        }

        double farmerPh = request.getSoilPh();
        double min = minPh != null ? minPh : 0.0;
        double max = maxPh != null ? maxPh : 14.0;

        if (farmerPh >= min && farmerPh <= max) {
            return new FactorResultDto("Soil pH", FactorStatus.MATCH, maxWeight, maxWeight,
                    "Your soil pH (" + farmerPh + ") is within the optimal range (" + min + " - " + max + ").");
        } else if (farmerPh >= (min - 0.5) && farmerPh <= (max + 0.5)) {
            return new FactorResultDto("Soil pH", FactorStatus.PARTIAL, maxWeight * 0.5, maxWeight,
                    "Your soil pH (" + farmerPh + ") is slightly outside optimal range (" + min + " - " + max + ") but tolerable.");
        } else {
            return new FactorResultDto("Soil pH", FactorStatus.MISMATCH, 0.0, maxWeight,
                    "Your soil pH (" + farmerPh + ") is outside the suitable range (" + min + " - " + max + ") for this crop.");
        }
    }

    private FactorResultDto evaluateWaterAvailability(CropRecommendationRequest request, CropRequirement req) {
        double maxWeight = 15.0;
        WaterAvailability reqMin = req.getMinWaterAvailability();
        WaterAvailability reqMax = req.getMaxWaterAvailability();
        WaterAvailability fWater = request.getWaterAvailability();

        if (reqMin == null && reqMax == null) {
            return new FactorResultDto("Water Availability", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Water requirement range not specified for this crop.");
        }
        if (fWater == null) {
            return new FactorResultDto("Water Availability", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Water availability not supplied in request.");
        }

        int fOrd = fWater.ordinal();
        int minOrd = reqMin != null ? reqMin.ordinal() : 0;
        int maxOrd = reqMax != null ? reqMax.ordinal() : WaterAvailability.VERY_HIGH.ordinal();

        if (fOrd >= minOrd && fOrd <= maxOrd) {
            return new FactorResultDto("Water Availability", FactorStatus.MATCH, maxWeight, maxWeight,
                    "Water availability (" + fWater + ") matches crop requirement range.");
        } else if (fOrd == minOrd - 1) {
            if (request.getIrrigation() == IrrigationType.YES || request.getIrrigation() == IrrigationType.SEASONAL) {
                return new FactorResultDto("Water Availability", FactorStatus.PARTIAL, maxWeight * 0.5, maxWeight,
                        "Water availability is 1 level below minimum requirement, but irrigation is available to supplement.");
            } else {
                return new FactorResultDto("Water Availability", FactorStatus.PARTIAL, maxWeight * 0.33, maxWeight,
                        "Water availability (" + fWater + ") is slightly below minimum requirement (" + reqMin + ").");
            }
        } else if (fOrd > maxOrd) {
            return new FactorResultDto("Water Availability", FactorStatus.PARTIAL, maxWeight * 0.67, maxWeight,
                    "Water availability (" + fWater + ") exceeds crop requirement; adequate field drainage is necessary.");
        } else {
            return new FactorResultDto("Water Availability", FactorStatus.MISMATCH, 0.0, maxWeight,
                    "Water availability (" + fWater + ") is significantly below minimum requirement (" + reqMin + ").");
        }
    }

    private FactorResultDto evaluateRainfall(CropRecommendationRequest request, CropRequirement req) {
        double maxWeight = 15.0;
        RainfallLevel reqMin = req.getMinRainfall();
        RainfallLevel reqMax = req.getMaxRainfall();
        RainfallLevel fRain = request.getExpectedRainfall();

        if (reqMin == null && reqMax == null) {
            return new FactorResultDto("Expected Rainfall", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Rainfall requirement not specified for this crop.");
        }
        if (fRain == null) {
            return new FactorResultDto("Expected Rainfall", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Expected rainfall not supplied in request.");
        }

        int fOrd = fRain.ordinal();
        int minOrd = reqMin != null ? reqMin.ordinal() : 0;
        int maxOrd = reqMax != null ? reqMax.ordinal() : RainfallLevel.VERY_HIGH.ordinal();

        if (fOrd >= minOrd && fOrd <= maxOrd) {
            return new FactorResultDto("Expected Rainfall", FactorStatus.MATCH, maxWeight, maxWeight,
                    "Expected rainfall (" + fRain + ") matches crop requirement range.");
        } else if (Math.abs(fOrd - minOrd) == 1 || Math.abs(fOrd - maxOrd) == 1) {
            return new FactorResultDto("Expected Rainfall", FactorStatus.PARTIAL, maxWeight * 0.5, maxWeight,
                    "Expected rainfall (" + fRain + ") is close to crop requirement range.");
        } else {
            return new FactorResultDto("Expected Rainfall", FactorStatus.MISMATCH, 0.0, maxWeight,
                    "Expected rainfall (" + fRain + ") is outside crop requirement range.");
        }
    }

    private FactorResultDto evaluateSeason(CropRecommendationRequest request, CropRequirement req) {
        double maxWeight = 15.0;
        Set<Season> suitableSeasons = req.getSuitableSeasons();
        Season fSeason = request.getSeason();

        if (suitableSeasons == null || suitableSeasons.isEmpty()) {
            return new FactorResultDto("Season", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Suitable seasons not specified for this crop.");
        }
        if (fSeason == null) {
            return new FactorResultDto("Season", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Target season not supplied in request.");
        }

        if (suitableSeasons.contains(fSeason)) {
            return new FactorResultDto("Season", FactorStatus.MATCH, maxWeight, maxWeight,
                    fSeason + " season matches the growing period for this crop.");
        } else {
            return new FactorResultDto("Season", FactorStatus.MISMATCH, 0.0, maxWeight,
                    fSeason + " is not a suitable growing season for this crop.");
        }
    }

    private FactorResultDto evaluateTemperature(CropRecommendationRequest request, CropRequirement req) {
        double maxWeight = 10.0;
        TemperatureRange minTemp = req.getMinTemperatureRange();
        TemperatureRange maxTemp = req.getMaxTemperatureRange();
        TemperatureRange fTemp = request.getTemperatureRange();

        if (minTemp == null && maxTemp == null) {
            return new FactorResultDto("Temperature Range", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Temperature requirement not specified for this crop.");
        }
        if (fTemp == null) {
            return new FactorResultDto("Temperature Range", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Temperature range not supplied in request.");
        }

        int fOrd = fTemp.ordinal();
        int minOrd = minTemp != null ? minTemp.ordinal() : 0;
        int maxOrd = maxTemp != null ? maxTemp.ordinal() : TemperatureRange.HOT.ordinal();

        if (fOrd >= minOrd && fOrd <= maxOrd) {
            return new FactorResultDto("Temperature Range", FactorStatus.MATCH, maxWeight, maxWeight,
                    "Temperature range (" + fTemp + ") is optimal for this crop.");
        } else if (Math.abs(fOrd - minOrd) == 1 || Math.abs(fOrd - maxOrd) == 1) {
            return new FactorResultDto("Temperature Range", FactorStatus.PARTIAL, maxWeight * 0.5, maxWeight,
                    "Temperature range (" + fTemp + ") is acceptable but slightly off optimal.");
        } else {
            return new FactorResultDto("Temperature Range", FactorStatus.MISMATCH, 0.0, maxWeight,
                    "Temperature range (" + fTemp + ") is unsuitable for this crop.");
        }
    }

    private FactorResultDto evaluateIrrigation(CropRecommendationRequest request, CropRequirement req) {
        double maxWeight = 5.0;
        Boolean irrigationRequired = req.getIrrigationRequired();
        IrrigationType fIrrigation = request.getIrrigation();

        if (irrigationRequired == null || fIrrigation == null) {
            return new FactorResultDto("Irrigation Availability", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Irrigation requirement or status not evaluated.");
        }

        if (Boolean.TRUE.equals(irrigationRequired)) {
            if (fIrrigation == IrrigationType.YES) {
                return new FactorResultDto("Irrigation Availability", FactorStatus.MATCH, maxWeight, maxWeight,
                        "Irrigation required by crop is available on farm.");
            } else if (fIrrigation == IrrigationType.SEASONAL) {
                return new FactorResultDto("Irrigation Availability", FactorStatus.PARTIAL, maxWeight * 0.5, maxWeight,
                        "Irrigation is seasonally available for a crop that requires regular irrigation.");
            } else {
                return new FactorResultDto("Irrigation Availability", FactorStatus.MISMATCH, 0.0, maxWeight,
                        "Crop requires irrigation but farm has no irrigation facilities.");
            }
        } else {
            return new FactorResultDto("Irrigation Availability", FactorStatus.MATCH, maxWeight, maxWeight,
                    "Crop does not strictly require irrigation; rainfed and residual moisture supported.");
        }
    }

    private FactorResultDto evaluateRiskLevel(CropRequirement req) {
        double maxWeight = 10.0;
        CropRiskLevel riskLevel = req.getRiskLevel();

        if (riskLevel == null) {
            return new FactorResultDto("Crop Risk Level", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                    "Risk level not specified for this crop.");
        }

        switch (riskLevel) {
            case LOW:
                return new FactorResultDto("Crop Risk Level", FactorStatus.MATCH, maxWeight, maxWeight,
                        "Low agronomic risk crop; stable yield characteristics under standard management.");
            case MEDIUM:
                return new FactorResultDto("Crop Risk Level", FactorStatus.MATCH, maxWeight * 0.7, maxWeight,
                        "Medium agronomic risk crop; requires standard pest and moisture management.");
            case HIGH:
                return new FactorResultDto("Crop Risk Level", FactorStatus.PARTIAL, maxWeight * 0.4, maxWeight,
                        "High risk crop; sensitive to pest pressure and weather fluctuations.");
            default:
                return new FactorResultDto("Crop Risk Level", FactorStatus.NOT_EVALUATED, 0.0, maxWeight,
                        "Unknown risk level.");
        }
    }

    private double calculatePriorityBonus(CropRecommendationRequest request, CropRequirement req,
                                         FactorResultDto rainfallFactor, FactorResultDto tempFactor) {
        Set<String> priorities = request.getPriorities();
        if (priorities == null || priorities.isEmpty()) {
            return 0.0;
        }

        double bonus = 0.0;
        for (String priority : priorities) {
            if ("Lower water requirement".equalsIgnoreCase(priority)) {
                if (req.getMinWaterAvailability() == WaterAvailability.LOW || req.getMinWaterAvailability() == WaterAvailability.VERY_LOW) {
                    bonus += 2.0;
                }
            } else if ("Lower crop risk".equalsIgnoreCase(priority)) {
                if (req.getRiskLevel() == CropRiskLevel.LOW) {
                    bonus += 2.0;
                }
            } else if ("Sustainable farming".equalsIgnoreCase(priority)) {
                if (req.getSustainabilityNotes() != null && !req.getSustainabilityNotes().trim().isEmpty()) {
                    bonus += 2.0;
                }
            } else if ("Better climate suitability".equalsIgnoreCase(priority)) {
                if (rainfallFactor.getStatus() == FactorStatus.MATCH && tempFactor.getStatus() == FactorStatus.MATCH) {
                    bonus += 2.0;
                }
            } else if ("Shorter growing period".equalsIgnoreCase(priority)) {
                String period = req.getGrowingPeriodDescription();
                if (period != null && (period.contains("85") || period.contains("90") || period.contains("100") || period.toLowerCase().contains("short"))) {
                    bonus += 2.0;
                }
            } else if ("Market opportunity".equalsIgnoreCase(priority)) {
                // Market data is not evaluated yet in Step 7H (0 points awarded)
            }
        }
        return bonus;
    }

    private String getSuitabilityCategory(double score) {
        if (score >= 90.0) return "EXCELLENT";
        if (score >= 75.0) return "GOOD";
        if (score >= 60.0) return "MODERATE";
        if (score >= 40.0) return "LOW";
        return "POOR";
    }

    private String generateOverallExplanation(String cropName, String suitability, double score, List<FactorResultDto> factors) {
        long matchCount = factors.stream().filter(f -> f.getStatus() == FactorStatus.MATCH).count();
        long mismatchCount = factors.stream().filter(f -> f.getStatus() == FactorStatus.MISMATCH).count();
        long notEvaluatedCount = factors.stream().filter(f -> f.getStatus() == FactorStatus.NOT_EVALUATED).count();

        StringBuilder sb = new StringBuilder();
        sb.append(cropName).append(" shows ").append(suitability.toLowerCase()).append(" suitability (")
                .append(score).append("/100) based on ").append(matchCount).append(" matching factors");

        if (mismatchCount > 0) {
            sb.append(" and ").append(mismatchCount).append(" mismatching factors");
        }
        sb.append(". Recommendation is based on verified ICAR agronomic guidelines and your supplied farm conditions.");

        if (notEvaluatedCount > 0) {
            sb.append(" Some suitability factors could not be evaluated because verified crop requirement data was unavailable.");
        }

        return sb.toString();
    }
}
