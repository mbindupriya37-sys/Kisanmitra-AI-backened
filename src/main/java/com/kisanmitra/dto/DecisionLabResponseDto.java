package com.kisanmitra.dto;

import java.time.LocalDateTime;

public class DecisionLabResponseDto {

    private Long requestId;
    private CropRecommendationResponseDto originalConditions;
    private DecisionLabRequestDto simulatedConditions;
    private CropRecommendationResultListDto originalResults;
    private CropRecommendationResultListDto simulatedResults;
    private DecisionLabComparisonDto comparison;
    private String disclaimer;
    private LocalDateTime timestamp;

    public DecisionLabResponseDto() {
    }

    public DecisionLabResponseDto(Long requestId, CropRecommendationResponseDto originalConditions,
                                  DecisionLabRequestDto simulatedConditions,
                                  CropRecommendationResultListDto originalResults,
                                  CropRecommendationResultListDto simulatedResults,
                                  DecisionLabComparisonDto comparison, String disclaimer,
                                  LocalDateTime timestamp) {
        this.requestId = requestId;
        this.originalConditions = originalConditions;
        this.simulatedConditions = simulatedConditions;
        this.originalResults = originalResults;
        this.simulatedResults = simulatedResults;
        this.comparison = comparison;
        this.disclaimer = disclaimer;
        this.timestamp = timestamp;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public CropRecommendationResponseDto getOriginalConditions() {
        return originalConditions;
    }

    public void setOriginalConditions(CropRecommendationResponseDto originalConditions) {
        this.originalConditions = originalConditions;
    }

    public DecisionLabRequestDto getSimulatedConditions() {
        return simulatedConditions;
    }

    public void setSimulatedConditions(DecisionLabRequestDto simulatedConditions) {
        this.simulatedConditions = simulatedConditions;
    }

    public CropRecommendationResultListDto getOriginalResults() {
        return originalResults;
    }

    public void setOriginalResults(CropRecommendationResultListDto originalResults) {
        this.originalResults = originalResults;
    }

    public CropRecommendationResultListDto getSimulatedResults() {
        return simulatedResults;
    }

    public void setSimulatedResults(CropRecommendationResultListDto simulatedResults) {
        this.simulatedResults = simulatedResults;
    }

    public DecisionLabComparisonDto getComparison() {
        return comparison;
    }

    public void setComparison(DecisionLabComparisonDto comparison) {
        this.comparison = comparison;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
