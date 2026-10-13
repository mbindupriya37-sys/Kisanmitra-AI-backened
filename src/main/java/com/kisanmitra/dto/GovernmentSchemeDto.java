package com.kisanmitra.dto;

import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.entity.SchemeCategory;

import java.time.LocalDateTime;

public class GovernmentSchemeDto {

    private Long id;
    private String name;
    private SchemeCategory category;
    private String categoryDisplayName;
    private String ministry;
    private String purpose;
    private String eligibleApplicants;
    private String benefits;
    private String requiredDocuments;
    private String applicationProcess;
    private String officialPortalUrl;
    private String sourceName;
    private String lastVerifiedDate;
    private String verificationStatus;
    private LocalDateTime updatedAt;

    public GovernmentSchemeDto() {
    }

    public GovernmentSchemeDto(GovernmentScheme entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.category = entity.getCategory();
            this.categoryDisplayName = formatCategoryName(entity.getCategory());
            this.ministry = entity.getMinistry();
            this.purpose = entity.getPurpose();
            this.eligibleApplicants = entity.getEligibleApplicants();
            this.benefits = entity.getBenefits();
            this.requiredDocuments = entity.getRequiredDocuments();
            this.applicationProcess = entity.getApplicationProcess();
            this.officialPortalUrl = entity.getOfficialPortalUrl();
            this.sourceName = entity.getSourceName();
            this.lastVerifiedDate = entity.getLastVerifiedDate();
            this.verificationStatus = entity.getVerificationStatus();
            this.updatedAt = entity.getUpdatedAt();
        }
    }

    private String formatCategoryName(SchemeCategory cat) {
        if (cat == null) return "General";
        switch (cat) {
            case FINANCIAL_SUPPORT: return "Financial Support";
            case CROP_INSURANCE: return "Crop Insurance";
            case IRRIGATION: return "Irrigation";
            case SOLAR_ENERGY: return "Solar & Energy";
            case MACHINERY: return "Machinery & Equipment";
            case SOIL_HEALTH: return "Soil Health";
            default: return "General";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SchemeCategory getCategory() {
        return category;
    }

    public void setCategory(SchemeCategory category) {
        this.category = category;
        this.categoryDisplayName = formatCategoryName(category);
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }

    public String getMinistry() {
        return ministry;
    }

    public void setMinistry(String ministry) {
        this.ministry = ministry;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getEligibleApplicants() {
        return eligibleApplicants;
    }

    public void setEligibleApplicants(String eligibleApplicants) {
        this.eligibleApplicants = eligibleApplicants;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(String requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    public String getApplicationProcess() {
        return applicationProcess;
    }

    public void setApplicationProcess(String applicationProcess) {
        this.applicationProcess = applicationProcess;
    }

    public String getOfficialPortalUrl() {
        return officialPortalUrl;
    }

    public void setOfficialPortalUrl(String officialPortalUrl) {
        this.officialPortalUrl = officialPortalUrl;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
