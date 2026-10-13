package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "public_procurement_observations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_procurement_dated_snapshot",
            columnNames = {"commodity", "marketing_season_type", "marketing_season_year", "state_name", "reporting_as_of_date"}
        )
    }
)
public class PublicProcurementObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String commodity; // WHEAT, RICE

    @Column(name = "marketing_season_type", nullable = false, length = 20)
    private String marketingSeasonType; // KMS, RMS

    @Column(name = "marketing_season_year", nullable = false, length = 20)
    private String marketingSeasonYear; // 2023-24, 2024-25

    @Column(name = "geographic_level", nullable = false, length = 30)
    private String geographicLevel = "NATIONAL";

    @Column(name = "state_name", nullable = false, length = 100)
    private String stateName = "ALL_INDIA";

    @Column(name = "reporting_as_of_date", nullable = false)
    private LocalDate reportingAsOfDate;

    @Column(name = "procurement_stage", nullable = false, length = 30)
    private String procurementStage; // PROGRESSIVE, FINAL

    @Column(name = "progressive_quantity_lmt", nullable = false, precision = 14, scale = 4)
    private BigDecimal progressiveQuantityLmt;

    @Column(name = "target_procurement_lmt", precision = 14, scale = 4)
    private BigDecimal targetProcurementLmt;

    @Column(name = "source_organization", nullable = false, length = 100)
    private String sourceOrganization = "DFPD_FCI";

    @Column(name = "source_reference_url", length = 500)
    private String sourceReferenceUrl;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PublicProcurementObservation() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public String getMarketingSeasonType() { return marketingSeasonType; }
    public void setMarketingSeasonType(String marketingSeasonType) { this.marketingSeasonType = marketingSeasonType; }

    public String getMarketingSeasonYear() { return marketingSeasonYear; }
    public void setMarketingSeasonYear(String marketingSeasonYear) { this.marketingSeasonYear = marketingSeasonYear; }

    public String getGeographicLevel() { return geographicLevel; }
    public void setGeographicLevel(String geographicLevel) { this.geographicLevel = geographicLevel; }

    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }

    public LocalDate getReportingAsOfDate() { return reportingAsOfDate; }
    public void setReportingAsOfDate(LocalDate reportingAsOfDate) { this.reportingAsOfDate = reportingAsOfDate; }

    public String getProcurementStage() { return procurementStage; }
    public void setProcurementStage(String procurementStage) { this.procurementStage = procurementStage; }

    public BigDecimal getProgressiveQuantityLmt() { return progressiveQuantityLmt; }
    public void setProgressiveQuantityLmt(BigDecimal progressiveQuantityLmt) { this.progressiveQuantityLmt = progressiveQuantityLmt; }

    public BigDecimal getTargetProcurementLmt() { return targetProcurementLmt; }
    public void setTargetProcurementLmt(BigDecimal targetProcurementLmt) { this.targetProcurementLmt = targetProcurementLmt; }

    public String getSourceOrganization() { return sourceOrganization; }
    public void setSourceOrganization(String sourceOrganization) { this.sourceOrganization = sourceOrganization; }

    public String getSourceReferenceUrl() { return sourceReferenceUrl; }
    public void setSourceReferenceUrl(String sourceReferenceUrl) { this.sourceReferenceUrl = sourceReferenceUrl; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
