package com.kisanmitra.dto;

import com.kisanmitra.entity.CropDiseaseKnowledge;

public class CropDiseaseKnowledgeDto {

    private Long id;
    private String cropName;
    private String diseaseName;
    private String scientificName;
    private String affectedPlantParts;
    private String symptomsDescription;
    private String nonChemicalManagement;
    private String culturalPractices;
    private String kvkExtensionAdvisory;
    private String sourceName;
    private String sourceUrl;
    private String lastVerifiedDate;
    private String verificationStatus;

    public CropDiseaseKnowledgeDto() {
    }

    public CropDiseaseKnowledgeDto(CropDiseaseKnowledge entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.cropName = entity.getCropName();
            this.diseaseName = entity.getDiseaseName();
            this.scientificName = entity.getScientificName();
            this.affectedPlantParts = entity.getAffectedPlantParts();
            this.symptomsDescription = entity.getSymptomsDescription();
            this.nonChemicalManagement = entity.getNonChemicalManagement();
            this.culturalPractices = entity.getCulturalPractices();
            this.kvkExtensionAdvisory = entity.getKvkExtensionAdvisory();
            this.sourceName = entity.getSourceName();
            this.sourceUrl = entity.getSourceUrl();
            this.lastVerifiedDate = entity.getLastVerifiedDate();
            this.verificationStatus = entity.getVerificationStatus();
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
}
