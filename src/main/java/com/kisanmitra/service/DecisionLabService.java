package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.CropRecommendationRequestRepository;
import com.kisanmitra.repository.CropRequirementRepository;
import com.kisanmitra.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DecisionLabService {

    private static final String DISCLAIMER_TEXT = "Simulations are decision-support indicators based on available verified crop knowledge and selected parameters. They do not guarantee yield, profit, or farming success.";

    private final CropRecommendationRequestRepository cropRecommendationRequestRepository;
    private final UserRepository userRepository;
    private final CropRequirementRepository cropRequirementRepository;
    private final CropRecommendationEngine cropRecommendationEngine;

    public DecisionLabService(CropRecommendationRequestRepository cropRecommendationRequestRepository,
                              UserRepository userRepository,
                              CropRequirementRepository cropRequirementRepository,
                              CropRecommendationEngine cropRecommendationEngine) {
        this.cropRecommendationRequestRepository = cropRecommendationRequestRepository;
        this.userRepository = userRepository;
        this.cropRequirementRepository = cropRequirementRepository;
        this.cropRecommendationEngine = cropRecommendationEngine;
    }

    @Transactional(readOnly = true)
    public DecisionLabResponseDto simulate(DecisionLabRequestDto dto) {
        User farmer = getAuthenticatedFarmer();

        CropRecommendationRequest originalReq = cropRecommendationRequestRepository
                .findById(dto.getRequestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment request not found for ID: " + dto.getRequestId()));

        if (!originalReq.getFarmer().getId().equals(farmer.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment request not found or not owned by you. ID: " + dto.getRequestId());
        }

        // Validate soil pH when provided
        if (!Boolean.TRUE.equals(dto.getSoilPhUnknown()) && dto.getSoilPh() != null) {
            if (dto.getSoilPh() < 4.0 || dto.getSoilPh() > 9.5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Soil pH must be between 4.0 and 9.5");
            }
        }

        List<CropRequirement> activeRequirements = cropRequirementRepository.findByActiveTrue();

        // 1. Evaluate Original Assessment
        List<CropRecommendationResultDto> originalResults = cropRecommendationEngine.evaluateAllCrops(originalReq, activeRequirements);

        // 2. Construct In-Memory Simulated Assessment (Temporary, Not Persisted)
        CropRecommendationRequest simulatedReq = new CropRecommendationRequest();
        simulatedReq.setId(originalReq.getId());
        simulatedReq.setFarmer(farmer);
        simulatedReq.setState(originalReq.getState());
        simulatedReq.setDistrict(originalReq.getDistrict());
        simulatedReq.setVillage(originalReq.getVillage());
        simulatedReq.setSoilType(originalReq.getSoilType());
        simulatedReq.setSoilPh(Boolean.TRUE.equals(dto.getSoilPhUnknown()) ? null : dto.getSoilPh());
        simulatedReq.setSoilPhUnknown(Boolean.TRUE.equals(dto.getSoilPhUnknown()));
        simulatedReq.setWaterAvailability(dto.getWaterAvailability());
        simulatedReq.setIrrigation(dto.getIrrigation());
        simulatedReq.setExpectedRainfall(dto.getExpectedRainfall());
        simulatedReq.setSeason(dto.getSeason());
        simulatedReq.setTemperatureRange(dto.getTemperatureRange());
        simulatedReq.setFarmSize(originalReq.getFarmSize());
        simulatedReq.setPriorities(originalReq.getPriorities());
        simulatedReq.setStatus(RecommendationStatus.COMPLETED);

        // 3. Evaluate Simulated Assessment
        List<CropRecommendationResultDto> simulatedResults = cropRecommendationEngine.evaluateAllCrops(simulatedReq, activeRequirements);

        // Wrap results into ResultListDtos
        CropRecommendationResultListDto origListDto = new CropRecommendationResultListDto(
                originalReq.getId(), RecommendationStatus.COMPLETED, originalReq.getCreatedAt(), originalResults, DISCLAIMER_TEXT
        );

        CropRecommendationResultListDto simListDto = new CropRecommendationResultListDto(
                originalReq.getId(), RecommendationStatus.COMPLETED, LocalDateTime.now(), simulatedResults, DISCLAIMER_TEXT
        );

        // 4. Compute Comparison Metrics
        DecisionLabComparisonDto comparison = computeComparison(originalResults, simulatedResults);

        CropRecommendationResponseDto origCondDto = CropRecommendationResponseDto.fromEntity(originalReq, "Original conditions");

        return new DecisionLabResponseDto(
                originalReq.getId(),
                origCondDto,
                dto,
                origListDto,
                simListDto,
                comparison,
                DISCLAIMER_TEXT,
                LocalDateTime.now()
        );
    }

    private DecisionLabComparisonDto computeComparison(List<CropRecommendationResultDto> origResults,
                                                         List<CropRecommendationResultDto> simResults) {
        if (origResults.isEmpty() || simResults.isEmpty()) {
            return new DecisionLabComparisonDto(null, 0.0, "N/A", null, 0.0, "N/A", 0.0, false, "No crops evaluated", new ArrayList<>());
        }

        CropRecommendationResultDto origTop = origResults.get(0);
        CropRecommendationResultDto simTop = simResults.get(0);

        double origScore = origTop.getOverallScore() != null ? origTop.getOverallScore() : 0.0;
        double simScore = simTop.getOverallScore() != null ? simTop.getOverallScore() : 0.0;

        double scoreDiff = Math.round((simScore - origScore) * 10.0) / 10.0;
        boolean topCropChanged = origTop.getCrop() != null && simTop.getCrop() != null
                && !origTop.getCrop().getId().equals(simTop.getCrop().getId());

        StringBuilder summaryBuilder = new StringBuilder();
        if (topCropChanged) {
            summaryBuilder.append("Under simulated conditions, top recommendation changed from ")
                    .append(origTop.getCrop().getName())
                    .append(" (").append(origScore).append(") to ")
                    .append(simTop.getCrop().getName())
                    .append(" (").append(simScore).append("). ");
        } else {
            summaryBuilder.append("Top recommendation remains ")
                    .append(origTop.getCrop().getName())
                    .append(". ");
            if (scoreDiff > 0) {
                summaryBuilder.append("Suitability score increased by ").append(scoreDiff).append(" points.");
            } else if (scoreDiff < 0) {
                summaryBuilder.append("Suitability score decreased by ").append(Math.abs(scoreDiff)).append(" points.");
            } else {
                summaryBuilder.append("Suitability indicator score remains unchanged.");
            }
        }

        // Compare factor-level results for the original top crop in both runs
        List<FactorComparisonDto> factorComparisons = new ArrayList<>();
        List<FactorResultDto> simFactors = simTop.getFactors() != null ? simTop.getFactors() : Collections.emptyList();
        Map<String, FactorResultDto> simFactorsMap = simFactors.stream()
                .collect(Collectors.toMap(f -> f.getFactorName().toLowerCase(), f -> f, (f1, f2) -> f1));

        if (origTop.getFactors() != null) {
            for (FactorResultDto origF : origTop.getFactors()) {
                String key = origF.getFactorName().toLowerCase();
                FactorResultDto simF = simFactorsMap.get(key);

                FactorStatus origSt = origF.getStatus();
                FactorStatus simSt = simF != null ? simF.getStatus() : FactorStatus.NOT_EVALUATED;
                double origFScore = origF.getScore() != null ? origF.getScore() : 0.0;
                double simFScore = simF != null && simF.getScore() != null ? simF.getScore() : 0.0;

                boolean changed = origSt != simSt || origFScore != simFScore;
                String impact = "Unchanged";
                if (simFScore > origFScore) {
                    impact = "Improved";
                } else if (simFScore < origFScore) {
                    impact = "Degraded";
                }

                factorComparisons.add(new FactorComparisonDto(
                        origF.getFactorName(),
                        origSt,
                        simSt,
                        origFScore,
                        simFScore,
                        changed,
                        impact
                ));
            }
        }

        return new DecisionLabComparisonDto(
                origTop.getCrop(),
                origScore,
                origTop.getSuitability(),
                simTop.getCrop(),
                simScore,
                simTop.getSuitability(),
                scoreDiff,
                topCropChanged,
                summaryBuilder.toString(),
                factorComparisons
        );
    }

    private User getAuthenticatedFarmer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        String email = authentication.getName();
        User farmer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer profile not found for user: " + email));

        if (farmer.getRole() != Role.FARMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only users with FARMER role can access the Decision Lab");
        }

        return farmer;
    }
}
