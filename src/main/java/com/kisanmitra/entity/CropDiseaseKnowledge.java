package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crop_disease_knowledge")
public class CropDiseaseKnowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private String diseaseName;

    @Column(nullable = false)
    private String scientificName;

    @Column(nullable = false)
    private String affectedPlantParts; // e.g. Leaves, Stem, Fruit, Roots, Whole Plant

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptomsDescription;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String nonChemicalManagement;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String culturalPractices;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String kvkExtensionAdvisory;

    @Column(nullable = false)
    private String sourceName;

    @Column(nullable = false)
    private String sourceUrl;

    @Column(nullable = false)
    private String lastVerifiedDate;

    @Column(nullable = false)
    private String verificationStatus; // e.g. VERIFIED, VERIFICATION_REQUIRED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CropDiseaseKnowledge() {
    }

    public CropDiseaseKnowledge(String cropName, String diseaseName, String scientificName,
                                String affectedPlantParts, String symptomsDescription,
                                String nonChemicalManagement, String culturalPractices,
                                String kvkExtensionAdvisory, String sourceName, String sourceUrl,
                                String lastVerifiedDate, String verificationStatus) {
        this.cropName = cropName;
        this.diseaseName = diseaseName;
        this.scientificName = scientificName;
        this.affectedPlantParts = affectedPlantParts;
        this.symptomsDescription = symptomsDescription;
        this.nonChemicalManagement = nonChemicalManagement;
        this.culturalPractices = culturalPractices;
        this.kvkExtensionAdvisory = kvkExtensionAdvisory;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.lastVerifiedDate = lastVerifiedDate;
        this.verificationStatus = verificationStatus;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getAffectedPlantParts() {
        return affectedPlantParts;
    }

    public void setAffectedPlantParts(String affectedPlantParts) {
        this.affectedPlantParts = affectedPlantParts;
    }

    public String getSymptomsDescription() {
        return symptomsDescription;
    }

    public void setSymptomsDescription(String symptomsDescription) {
        this.symptomsDescription = symptomsDescription;
    }

    public String getNonChemicalManagement() {
        return nonChemicalManagement;
    }

    public void setNonChemicalManagement(String nonChemicalManagement) {
        this.nonChemicalManagement = nonChemicalManagement;
    }

    public String getCulturalPractices() {
        return culturalPractices;
    }

    public void setCulturalPractices(String culturalPractices) {
        this.culturalPractices = culturalPractices;
    }

    public String getKvkExtensionAdvisory() {
        return kvkExtensionAdvisory;
    }

    public void setKvkExtensionAdvisory(String kvkExtensionAdvisory) {
        this.kvkExtensionAdvisory = kvkExtensionAdvisory;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getLastVerifiedDate() {
        return lastVerifiedDate;
    }

    public void setLastVerifiedDate(String lastVerifiedDate) {
        this.lastVerifiedDate = lastVerifiedDate;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
