package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "government_schemes")
public class GovernmentScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchemeCategory category;

    @Column(nullable = false)
    private String ministry;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String purpose;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String eligibleApplicants;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String benefits;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String requiredDocuments;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String applicationProcess;

    @Column(nullable = false)
    private String officialPortalUrl;

    @Column(nullable = false)
    private String sourceName;

    @Column(nullable = false)
    private String lastVerifiedDate;

    @Column(nullable = false)
    private String verificationStatus; // e.g. VERIFIED, VERIFICATION_REQUIRED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public GovernmentScheme() {
    }

    public GovernmentScheme(String name, SchemeCategory category, String ministry, String purpose,
                            String eligibleApplicants, String benefits, String requiredDocuments,
                            String applicationProcess, String officialPortalUrl, String sourceName,
                            String lastVerifiedDate, String verificationStatus) {
        this.name = name;
        this.category = category;
        this.ministry = ministry;
        this.purpose = purpose;
        this.eligibleApplicants = eligibleApplicants;
        this.benefits = benefits;
        this.requiredDocuments = requiredDocuments;
        this.applicationProcess = applicationProcess;
        this.officialPortalUrl = officialPortalUrl;
        this.sourceName = sourceName;
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
