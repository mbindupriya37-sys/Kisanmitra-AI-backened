package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "msp_price_notifications",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_msp_notif_statutory_identity",
            columnNames = {
                "crop_id", "commodity_name", "commodity_variety", 
                "marketing_season_type", "season_year", "gazette_notification_ref"
            }
        )
    },
    indexes = {
        @Index(
            name = "idx_msp_active_lookup",
            columnList = "crop_id, marketing_season_type, season_year, is_active"
        )
    }
)
public class MspPriceNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(name = "commodity_name", nullable = false, length = 100)
    private String commodityName;

    @Column(name = "commodity_variety", nullable = false, length = 100)
    private String commodityVariety;

    @Column(name = "marketing_season_type", nullable = false, length = 20)
    private String marketingSeasonType; // KMS, RMS

    @Column(name = "season_year", nullable = false, length = 20)
    private String seasonYear; // 2024-25, 2025-26

    @Column(name = "msp_inr_per_quintal", nullable = false, precision = 10, scale = 2)
    private BigDecimal mspInrPerQuintal;

    @Column(name = "bonus_inr_per_quintal", precision = 10, scale = 2)
    private BigDecimal bonusInrPerQuintal = BigDecimal.ZERO;

    @Column(name = "cost_a2_fl_inr_per_quintal", precision = 10, scale = 2)
    private BigDecimal costA2FlInrPerQuintal;

    @Column(name = "cost_c2_inr_per_quintal", precision = 10, scale = 2)
    private BigDecimal costC2InrPerQuintal;

    @Column(name = "gazette_notification_ref", nullable = false, length = 150)
    private String gazetteNotificationRef;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "superseded_by_id")
    private Long supersededById;

    @Column(name = "source_organization", nullable = false, length = 100)
    private String sourceOrganization = "CCEA_GOI";

    @Column(name = "source_reference_url", length = 500)
    private String sourceReferenceUrl;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public MspPriceNotification() {
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

    public Crop getCrop() { return crop; }
    public void setCrop(Crop crop) { this.crop = crop; }

    public String getCommodityName() { return commodityName; }
    public void setCommodityName(String commodityName) { this.commodityName = commodityName; }

    public String getCommodityVariety() { return commodityVariety; }
    public void setCommodityVariety(String commodityVariety) { this.commodityVariety = commodityVariety; }

    public String getMarketingSeasonType() { return marketingSeasonType; }
    public void setMarketingSeasonType(String marketingSeasonType) { this.marketingSeasonType = marketingSeasonType; }

    public String getSeasonYear() { return seasonYear; }
    public void setSeasonYear(String seasonYear) { this.seasonYear = seasonYear; }

    public BigDecimal getMspInrPerQuintal() { return mspInrPerQuintal; }
    public void setMspInrPerQuintal(BigDecimal mspInrPerQuintal) { this.mspInrPerQuintal = mspInrPerQuintal; }

    public BigDecimal getBonusInrPerQuintal() { return bonusInrPerQuintal; }
    public void setBonusInrPerQuintal(BigDecimal bonusInrPerQuintal) { this.bonusInrPerQuintal = bonusInrPerQuintal; }

    public BigDecimal getCostA2FlInrPerQuintal() { return costA2FlInrPerQuintal; }
    public void setCostA2FlInrPerQuintal(BigDecimal costA2FlInrPerQuintal) { this.costA2FlInrPerQuintal = costA2FlInrPerQuintal; }

    public BigDecimal getCostC2InrPerQuintal() { return costC2InrPerQuintal; }
    public void setCostC2InrPerQuintal(BigDecimal costC2InrPerQuintal) { this.costC2InrPerQuintal = costC2InrPerQuintal; }

    public String getGazetteNotificationRef() { return gazetteNotificationRef; }
    public void setGazetteNotificationRef(String gazetteNotificationRef) { this.gazetteNotificationRef = gazetteNotificationRef; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Long getSupersededById() { return supersededById; }
    public void setSupersededById(Long supersededById) { this.supersededById = supersededById; }

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
