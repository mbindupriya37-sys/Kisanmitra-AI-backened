package com.kisanmitra.dto;

import com.kisanmitra.entity.FactorStatus;

public class FactorResultDto {

    private String factorName;
    private FactorStatus status;
    private Double score;
    private Double maxWeight;
    private String explanation;

    public FactorResultDto() {
    }

    public FactorResultDto(String factorName, FactorStatus status, Double score, Double maxWeight, String explanation) {
        this.factorName = factorName;
        this.status = status;
        this.score = score;
        this.maxWeight = maxWeight;
        this.explanation = explanation;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public FactorStatus getStatus() {
        return status;
    }

    public void setStatus(FactorStatus status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
