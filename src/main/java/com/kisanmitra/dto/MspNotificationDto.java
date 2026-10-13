package com.kisanmitra.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MspNotificationDto {

    private String status; // ACTIVE, MISSING_NOTIFICATION, UNRESOLVED_MAPPING, SUPERSEDED
    private String cropName;
    private String commodityName;
    private String commodityVariety;
    private String marketingSeasonType;
    private String seasonYear;
    private BigDecimal mspInrPerQuintal;
    private BigDecimal bonusInrPerQuintal;
    private BigDecimal effectiveMspInrPerQuintal;
    private BigDecimal costA2FlInrPerQuintal;
    private BigDecimal costC2InrPerQuintal;
    private String gazetteNotificationRef;
    private LocalDate effectiveDate;
    private String statusExplanation;
    private ProvenanceMetaDto provenance;

    public MspNotificationDto() {
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

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

    public BigDecimal getEffectiveMspInrPerQuintal() { return effectiveMspInrPerQuintal; }
    public void setEffectiveMspInrPerQuintal(BigDecimal effectiveMspInrPerQuintal) { this.effectiveMspInrPerQuintal = effectiveMspInrPerQuintal; }

    public BigDecimal getCostA2FlInrPerQuintal() { return costA2FlInrPerQuintal; }
    public void setCostA2FlInrPerQuintal(BigDecimal costA2FlInrPerQuintal) { this.costA2FlInrPerQuintal = costA2FlInrPerQuintal; }

    public BigDecimal getCostC2InrPerQuintal() { return costC2InrPerQuintal; }
    public void setCostC2InrPerQuintal(BigDecimal costC2InrPerQuintal) { this.costC2InrPerQuintal = costC2InrPerQuintal; }

    public String getGazetteNotificationRef() { return gazetteNotificationRef; }
    public void setGazetteNotificationRef(String gazetteNotificationRef) { this.gazetteNotificationRef = gazetteNotificationRef; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getStatusExplanation() { return statusExplanation; }
    public void setStatusExplanation(String statusExplanation) { this.statusExplanation = statusExplanation; }

    public ProvenanceMetaDto getProvenance() { return provenance; }
    public void setProvenance(ProvenanceMetaDto provenance) { this.provenance = provenance; }
}
