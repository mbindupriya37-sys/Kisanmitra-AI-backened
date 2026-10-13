package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "public_buffer_stock_observations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_buffer_stock_milestone",
            columnNames = {"commodity", "observation_date"}
        )
    }
)
public class PublicBufferStockObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String commodity; // WHEAT, RICE, COARSE_GRAINS

    @Column(name = "observation_date", nullable = false)
    private LocalDate observationDate;

    @Column(name = "actual_central_pool_lmt", nullable = false, precision = 12, scale = 4)
    private BigDecimal actualCentralPoolLmt;

    @Column(name = "operational_norm_lmt", precision = 12, scale = 4)
    private BigDecimal operationalNormLmt;

    @Column(name = "strategic_reserve_norm_lmt", precision = 12, scale = 4)
    private BigDecimal strategicReserveNormLmt;

    @Column(name = "total_buffer_norm_lmt", precision = 12, scale = 4)
    private BigDecimal totalBufferNormLmt;

    @Column(name = "norm_milestone_date")
    private LocalDate normMilestoneDate;

    @Column(name = "comparison_status", nullable = false, length = 50)
    private String comparisonStatus; // EXACT_MILESTONE_MATCHED, PERIOD_MISMATCH_NO_COMPARISON

    @Column(name = "surplus_deficit_lmt", precision = 12, scale = 4)
    private BigDecimal surplusDeficitLmt;

    @Column(columnDefinition = "TEXT")
    private String notes;

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

    public PublicBufferStockObservation() {
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

    public LocalDate getObservationDate() { return observationDate; }
    public void setObservationDate(LocalDate observationDate) { this.observationDate = observationDate; }

    public BigDecimal getActualCentralPoolLmt() { return actualCentralPoolLmt; }
    public void setActualCentralPoolLmt(BigDecimal actualCentralPoolLmt) { this.actualCentralPoolLmt = actualCentralPoolLmt; }

    public BigDecimal getOperationalNormLmt() { return operationalNormLmt; }
    public void setOperationalNormLmt(BigDecimal operationalNormLmt) { this.operationalNormLmt = operationalNormLmt; }

    public BigDecimal getStrategicReserveNormLmt() { return strategicReserveNormLmt; }
    public void setStrategicReserveNormLmt(BigDecimal strategicReserveNormLmt) { this.strategicReserveNormLmt = strategicReserveNormLmt; }

    public BigDecimal getTotalBufferNormLmt() { return totalBufferNormLmt; }
    public void setTotalBufferNormLmt(BigDecimal totalBufferNormLmt) { this.totalBufferNormLmt = totalBufferNormLmt; }

    public LocalDate getNormMilestoneDate() { return normMilestoneDate; }
    public void setNormMilestoneDate(LocalDate normMilestoneDate) { this.normMilestoneDate = normMilestoneDate; }

    public String getComparisonStatus() { return comparisonStatus; }
    public void setComparisonStatus(String comparisonStatus) { this.comparisonStatus = comparisonStatus; }

    public BigDecimal getSurplusDeficitLmt() { return surplusDeficitLmt; }
    public void setSurplusDeficitLmt(BigDecimal surplusDeficitLmt) { this.surplusDeficitLmt = surplusDeficitLmt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

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
