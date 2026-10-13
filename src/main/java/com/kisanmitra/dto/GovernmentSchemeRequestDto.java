package com.kisanmitra.dto;

import com.kisanmitra.entity.SchemeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GovernmentSchemeRequestDto {

    @NotBlank(message = "Scheme name is required")
    private String name;

    @NotNull(message = "Scheme category is required")
    private SchemeCategory category;

    @NotBlank(message = "Ministry name is required")
    private String ministry;

    @NotBlank(message = "Scheme purpose is required")
    private String purpose;

    @NotBlank(message = "Eligible applicants criteria is required")
    private String eligibleApplicants;

    @NotBlank(message = "Scheme benefits description is required")
    private String benefits;

    @NotBlank(message = "Required documents list is required")
    private String requiredDocuments;

    @NotBlank(message = "Application process is required")
    private String applicationProcess;

    @NotBlank(message = "Official portal URL is required")
    private String officialPortalUrl;

    @NotBlank(message = "Source name is required")
    private String sourceName;

    private String lastVerifiedDate;
    private String verificationStatus;

    public GovernmentSchemeRequestDto() {
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
}
