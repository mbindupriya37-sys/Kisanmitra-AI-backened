package com.kisanmitra.dto;

import com.kisanmitra.entity.CropDiseaseAssessmentLog;

import java.time.LocalDateTime;

/**
 * Expert advisory projection of crop disease assessment logs.
 * Farmer email and sensitive PII are strictly excluded to preserve privacy.
 */
public class ExpertAdvisoryCaseDto {

    private Long id;
    private String cropName;
    private String affectedPart;
    private Integer symptomDurationDays;
    private String severityLevel;
    private String symptomsDescription;
    private String matchedDiseaseNames;
    private Boolean kvkConsultationRecommended;
    private LocalDateTime createdAt;

    public ExpertAdvisoryCaseDto() {
    }

    public ExpertAdvisoryCaseDto(CropDiseaseAssessmentLog log) {
        if (log != null) {
            this.id = log.getId();
            this.cropName = log.getCropName();
            this.affectedPart = log.getAffectedPart();
            this.symptomDurationDays = log.getSymptomDurationDays();
            this.severityLevel = log.getSeverityLevel();
            this.symptomsDescription = log.getSymptomsDescription();
            this.matchedDiseaseNames = log.getMatchedDiseaseNames();
            this.kvkConsultationRecommended = log.getKvkConsultationRecommended();
            this.createdAt = log.getCreatedAt();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getAffectedPart() {
        return affectedPart;
    }

    public void setAffectedPart(String affectedPart) {
        this.affectedPart = affectedPart;
    }

    public Integer getSymptomDurationDays() {
        return symptomDurationDays;
    }

    public void setSymptomDurationDays(Integer symptomDurationDays) {
        this.symptomDurationDays = symptomDurationDays;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getSymptomsDescription() {
        return symptomsDescription;
    }

    public void setSymptomsDescription(String symptomsDescription) {
        this.symptomsDescription = symptomsDescription;
    }

    public String getMatchedDiseaseNames() {
        return matchedDiseaseNames;
    }

    public void setMatchedDiseaseNames(String matchedDiseaseNames) {
        this.matchedDiseaseNames = matchedDiseaseNames;
    }

    public Boolean getKvkConsultationRecommended() {
        return kvkConsultationRecommended;
    }

    public void setKvkConsultationRecommended(Boolean kvkConsultationRecommended) {
        this.kvkConsultationRecommended = kvkConsultationRecommended;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
