package com.kisanmitra.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProcurementObservationDto {

    private String commodity;
    private String marketingSeasonType;
    private String marketingSeasonYear;
    private String geographicLevel;
    private String stateName;
    private LocalDate reportingAsOfDate;
    private String procurementStage;
    private BigDecimal progressiveQuantityLmt;
    private BigDecimal targetProcurementLmt;
    private ProvenanceMetaDto provenance;

    public ProcurementObservationDto() {
    }

    // Getters and Setters
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

    public ProvenanceMetaDto getProvenance() { return provenance; }
    public void setProvenance(ProvenanceMetaDto provenance) { this.provenance = provenance; }
}
