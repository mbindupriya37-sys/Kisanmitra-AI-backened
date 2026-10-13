package com.kisanmitra.dto;

import java.util.ArrayList;
import java.util.List;

public class DecisionLabComparisonDto {

    private CropSummaryDto originalTopCrop;
    private Double originalTopScore;
    private String originalTopSuitability;

    private CropSummaryDto simulatedTopCrop;
    private Double simulatedTopScore;
    private String simulatedTopSuitability;

    private Double scoreDifference;
    private Boolean topCropChanged;
    private String summaryExplanation;
    private List<FactorComparisonDto> topCropFactorComparisons = new ArrayList<>();

    public DecisionLabComparisonDto() {
    }

    public DecisionLabComparisonDto(CropSummaryDto originalTopCrop, Double originalTopScore, String originalTopSuitability,
                                     CropSummaryDto simulatedTopCrop, Double simulatedTopScore, String simulatedTopSuitability,
                                     Double scoreDifference, Boolean topCropChanged, String summaryExplanation,
                                     List<FactorComparisonDto> topCropFactorComparisons) {
        this.originalTopCrop = originalTopCrop;
        this.originalTopScore = originalTopScore;
        this.originalTopSuitability = originalTopSuitability;
        this.simulatedTopCrop = simulatedTopCrop;
        this.simulatedTopScore = simulatedTopScore;
        this.simulatedTopSuitability = simulatedTopSuitability;
        this.scoreDifference = scoreDifference;
        this.topCropChanged = topCropChanged;
        this.summaryExplanation = summaryExplanation;
        this.topCropFactorComparisons = topCropFactorComparisons != null ? topCropFactorComparisons : new ArrayList<>();
    }

    public CropSummaryDto getOriginalTopCrop() {
        return originalTopCrop;
    }

    public void setOriginalTopCrop(CropSummaryDto originalTopCrop) {
        this.originalTopCrop = originalTopCrop;
    }

    public Double getOriginalTopScore() {
        return originalTopScore;
    }

    public void setOriginalTopScore(Double originalTopScore) {
        this.originalTopScore = originalTopScore;
    }

    public String getOriginalTopSuitability() {
        return originalTopSuitability;
    }

    public void setOriginalTopSuitability(String originalTopSuitability) {
        this.originalTopSuitability = originalTopSuitability;
    }

    public CropSummaryDto getSimulatedTopCrop() {
        return simulatedTopCrop;
    }

    public void setSimulatedTopCrop(CropSummaryDto simulatedTopCrop) {
        this.simulatedTopCrop = simulatedTopCrop;
    }

    public Double getSimulatedTopScore() {
        return simulatedTopScore;
    }

    public void setSimulatedTopScore(Double simulatedTopScore) {
        this.simulatedTopScore = simulatedTopScore;
    }

    public String getSimulatedTopSuitability() {
        return simulatedTopSuitability;
    }

    public void setSimulatedTopSuitability(String simulatedTopSuitability) {
        this.simulatedTopSuitability = simulatedTopSuitability;
    }

    public Double getScoreDifference() {
        return scoreDifference;
    }

    public void setScoreDifference(Double scoreDifference) {
        this.scoreDifference = scoreDifference;
    }

    public Boolean getTopCropChanged() {
        return topCropChanged;
    }

    public void setTopCropChanged(Boolean topCropChanged) {
        this.topCropChanged = topCropChanged;
    }

    public String getSummaryExplanation() {
        return summaryExplanation;
    }

    public void setSummaryExplanation(String summaryExplanation) {
        this.summaryExplanation = summaryExplanation;
    }

    public List<FactorComparisonDto> getTopCropFactorComparisons() {
        return topCropFactorComparisons;
    }

    public void setTopCropFactorComparisons(List<FactorComparisonDto> topCropFactorComparisons) {
        this.topCropFactorComparisons = topCropFactorComparisons;
    }
}
