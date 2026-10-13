package com.kisanmitra.dto;

import com.kisanmitra.entity.FactorStatus;

public class FactorComparisonDto {

    private String factorName;
    private FactorStatus originalStatus;
    private FactorStatus simulatedStatus;
    private Double originalScore;
    private Double simulatedScore;
    private Boolean statusChanged;
    private String impact; // Improved, Degraded, Unchanged

    public FactorComparisonDto() {
    }

    public FactorComparisonDto(String factorName, FactorStatus originalStatus, FactorStatus simulatedStatus,
                               Double originalScore, Double simulatedScore, Boolean statusChanged, String impact) {
        this.factorName = factorName;
        this.originalStatus = originalStatus;
        this.simulatedStatus = simulatedStatus;
        this.originalScore = originalScore;
        this.simulatedScore = simulatedScore;
        this.statusChanged = statusChanged;
        this.impact = impact;
    }

    public String getFactorName() {
        return factorName;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }

    public FactorStatus getOriginalStatus() {
        return originalStatus;
    }

    public void setOriginalStatus(FactorStatus originalStatus) {
        this.originalStatus = originalStatus;
    }

    public FactorStatus getSimulatedStatus() {
        return simulatedStatus;
    }

    public void setSimulatedStatus(FactorStatus simulatedStatus) {
        this.simulatedStatus = simulatedStatus;
    }

    public Double getOriginalScore() {
        return originalScore;
    }

    public void setOriginalScore(Double originalScore) {
        this.originalScore = originalScore;
    }

    public Double getSimulatedScore() {
        return simulatedScore;
    }

    public void setSimulatedScore(Double simulatedScore) {
        this.simulatedScore = simulatedScore;
    }

    public Boolean getStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(Boolean statusChanged) {
        this.statusChanged = statusChanged;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }
}
