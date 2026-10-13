package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crop_disease_assessment_logs")
public class CropDiseaseAssessmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String farmerEmail;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private String affectedPart;

    private Integer symptomDurationDays;

    @Column(nullable = false)
    private String severityLevel; // e.g. MILD, MODERATE, SEVERE

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptomsDescription;

    @Column(columnDefinition = "TEXT")
    private String matchedDiseaseNames;

    @Column(nullable = false)
    private Boolean kvkConsultationRecommended;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CropDiseaseAssessmentLog() {
    }

    public CropDiseaseAssessmentLog(String farmerEmail, String cropName, String affectedPart,
                                    Integer symptomDurationDays, String severityLevel,
                                    String symptomsDescription, String matchedDiseaseNames,
                                    Boolean kvkConsultationRecommended) {
        this.farmerEmail = farmerEmail;
        this.cropName = cropName;
        this.affectedPart = affectedPart;
        this.symptomDurationDays = symptomDurationDays;
        this.severityLevel = severityLevel;
        this.symptomsDescription = symptomsDescription;
        this.matchedDiseaseNames = matchedDiseaseNames;
        this.kvkConsultationRecommended = kvkConsultationRecommended;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFarmerEmail() {
        return farmerEmail;
    }

    public void setFarmerEmail(String farmerEmail) {
        this.farmerEmail = farmerEmail;
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
