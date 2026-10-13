package com.kisanmitra.dto;

import com.kisanmitra.entity.CropRiskLevel;
import java.util.ArrayList;
import java.util.List;

public class CropRecommendationResultDto {

    private CropSummaryDto crop;
    private Double overallScore;
    private String suitability;
    private CropRiskLevel riskLevel;
    private List<FactorResultDto> factors = new ArrayList<>();
    private String explanation;
    private String waterRequirement;
    private String growingPeriod;
    private String sustainabilityNotes;
    private SourceAttributionDto source;

    public CropRecommendationResultDto() {
    }

    public CropRecommendationResultDto(CropSummaryDto crop, Double overallScore, String suitability,
                                       CropRiskLevel riskLevel, List<FactorResultDto> factors,
                                       String explanation, String waterRequirement, String growingPeriod,
                                       String sustainabilityNotes, SourceAttributionDto source) {
        this.crop = crop;
        this.overallScore = overallScore;
        this.suitability = suitability;
        this.riskLevel = riskLevel;
        this.factors = factors != null ? factors : new ArrayList<>();
        this.explanation = explanation;
        this.waterRequirement = waterRequirement;
        this.growingPeriod = growingPeriod;
        this.sustainabilityNotes = sustainabilityNotes;
        this.source = source;
    }

    public CropSummaryDto getCrop() {
        return crop;
    }

    public void setCrop(CropSummaryDto crop) {
        this.crop = crop;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public String getSuitability() {
        return suitability;
    }

    public void setSuitability(String suitability) {
        this.suitability = suitability;
    }

    public CropRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(CropRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<FactorResultDto> getFactors() {
        return factors;
    }

    public void setFactors(List<FactorResultDto> factors) {
        this.factors = factors;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getWaterRequirement() {
        return waterRequirement;
    }

    public void setWaterRequirement(String waterRequirement) {
        this.waterRequirement = waterRequirement;
    }

    public String getGrowingPeriod() {
        return growingPeriod;
    }

    public void setGrowingPeriod(String growingPeriod) {
        this.growingPeriod = growingPeriod;
    }

    public String getSustainabilityNotes() {
        return sustainabilityNotes;
    }

    public void setSustainabilityNotes(String sustainabilityNotes) {
        this.sustainabilityNotes = sustainabilityNotes;
    }

    public SourceAttributionDto getSource() {
        return source;
    }

    public void setSource(SourceAttributionDto source) {
        this.source = source;
    }
}
