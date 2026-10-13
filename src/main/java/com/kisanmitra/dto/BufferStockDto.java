package com.kisanmitra.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BufferStockDto {

    private String commodity;
    private LocalDate observationDate;
    private BigDecimal actualCentralPoolLmt;
    private BigDecimal operationalNormLmt;
    private BigDecimal strategicReserveNormLmt;
    private BigDecimal totalBufferNormLmt;
    private LocalDate normMilestoneDate;
    private String comparisonStatus; // EXACT_MILESTONE_MATCHED, PERIOD_MISMATCH_NO_COMPARISON
    private BigDecimal surplusDeficitLmt;
    private String explanatoryNote;
    private ProvenanceMetaDto provenance;

    public BufferStockDto() {
    }

    // Getters and Setters
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

    public String getExplanatoryNote() { return explanatoryNote; }
    public void setExplanatoryNote(String explanatoryNote) { this.explanatoryNote = explanatoryNote; }

    public ProvenanceMetaDto getProvenance() { return provenance; }
    public void setProvenance(ProvenanceMetaDto provenance) { this.provenance = provenance; }
}
