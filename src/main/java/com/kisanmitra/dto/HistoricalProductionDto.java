package com.kisanmitra.dto;

import java.math.BigDecimal;
import java.util.List;

public class HistoricalProductionDto {

    private String cropName;
    private String state;
    private String district;
    private String boundaryVintage;
    private String boundaryNotice;
    private BigDecimal fiveYearAverageProductionTonnes;
    private List<ProductionObservationItem> observations;
    private ProvenanceMetaDto provenance;

    public HistoricalProductionDto() {
    }

    public static class ProductionObservationItem {
        private String agriculturalYear;
        private String season;
        private BigDecimal areaHectares;
        private BigDecimal rawProductionValue;
        private String rawUnit;
        private BigDecimal productionTonnes;
        private BigDecimal yieldKgPerHa;
        private BigDecimal conversionFactorApplied;
        private String conversionVerificationRef;
        private String sourceRevision;
        private String dataStatus;

        public ProductionObservationItem() {}

        public String getAgriculturalYear() { return agriculturalYear; }
        public void setAgriculturalYear(String agriculturalYear) { this.agriculturalYear = agriculturalYear; }

        public String getSeason() { return season; }
        public void setSeason(String season) { this.season = season; }

        public BigDecimal getAreaHectares() { return areaHectares; }
        public void setAreaHectares(BigDecimal areaHectares) { this.areaHectares = areaHectares; }

        public BigDecimal getRawProductionValue() { return rawProductionValue; }
        public void setRawProductionValue(BigDecimal rawProductionValue) { this.rawProductionValue = rawProductionValue; }

        public String getRawUnit() { return rawUnit; }
        public void setRawUnit(String rawUnit) { this.rawUnit = rawUnit; }

        public BigDecimal getProductionTonnes() { return productionTonnes; }
        public void setProductionTonnes(BigDecimal productionTonnes) { this.productionTonnes = productionTonnes; }

        public BigDecimal getYieldKgPerHa() { return yieldKgPerHa; }
        public void setYieldKgPerHa(BigDecimal yieldKgPerHa) { this.yieldKgPerHa = yieldKgPerHa; }

        public BigDecimal getConversionFactorApplied() { return conversionFactorApplied; }
        public void setConversionFactorApplied(BigDecimal conversionFactorApplied) { this.conversionFactorApplied = conversionFactorApplied; }

        public String getConversionVerificationRef() { return conversionVerificationRef; }
        public void setConversionVerificationRef(String conversionVerificationRef) { this.conversionVerificationRef = conversionVerificationRef; }

        public String getSourceRevision() { return sourceRevision; }
        public void setSourceRevision(String sourceRevision) { this.sourceRevision = sourceRevision; }

        public String getDataStatus() { return dataStatus; }
        public void setDataStatus(String dataStatus) { this.dataStatus = dataStatus; }
    }

    // Getters and Setters
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getBoundaryVintage() { return boundaryVintage; }
    public void setBoundaryVintage(String boundaryVintage) { this.boundaryVintage = boundaryVintage; }

    public String getBoundaryNotice() { return boundaryNotice; }
    public void setBoundaryNotice(String boundaryNotice) { this.boundaryNotice = boundaryNotice; }

    public BigDecimal getFiveYearAverageProductionTonnes() { return fiveYearAverageProductionTonnes; }
    public void setFiveYearAverageProductionTonnes(BigDecimal fiveYearAverageProductionTonnes) { this.fiveYearAverageProductionTonnes = fiveYearAverageProductionTonnes; }

    public List<ProductionObservationItem> getObservations() { return observations; }
    public void setObservations(List<ProductionObservationItem> observations) { this.observations = observations; }

    public ProvenanceMetaDto getProvenance() { return provenance; }
    public void setProvenance(ProvenanceMetaDto provenance) { this.provenance = provenance; }
}
