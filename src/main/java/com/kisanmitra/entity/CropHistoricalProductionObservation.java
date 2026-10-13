package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "crop_historical_production_observations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_prod_obs_natural_identity",
            columnNames = {
                "crop_id", "normalized_state", "normalized_district", 
                "boundary_vintage", "agricultural_year", "season", "source_revision"
            }
        )
    },
    indexes = {
        @Index(
            name = "idx_prod_active_search",
            columnList = "crop_id, normalized_state, normalized_district, season, is_active"
        )
    }
)
public class CropHistoricalProductionObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(name = "source_crop_name", nullable = false, length = 100)
    private String sourceCropName;

    @Column(name = "source_state", nullable = false, length = 100)
    private String sourceState;

    @Column(name = "source_district", nullable = false, length = 100)
    private String sourceDistrict;

    @Column(name = "normalized_state", nullable = false, length = 100)
    private String normalizedState;

    @Column(name = "normalized_district", nullable = false, length = 100)
    private String normalizedDistrict;

    @Column(name = "state_lgd_code", length = 20)
    private String stateLgdCode;

    @Column(name = "district_lgd_code", length = 20)
    private String districtLgdCode;

    @Column(name = "boundary_vintage", nullable = false, length = 50)
    private String boundaryVintage;

    @Column(name = "agricultural_year", nullable = false, length = 20)
    private String agriculturalYear;

    @Column(nullable = false, length = 30)
    private String season;

    @Column(name = "area_hectares", precision = 14, scale = 4)
    private BigDecimal areaHectares;

    @Column(name = "raw_production_value", precision = 14, scale = 4)
    private BigDecimal rawProductionValue;

    @Column(name = "raw_unit", nullable = false, length = 30)
    private String rawUnit;

    @Column(name = "production_tonnes", precision = 14, scale = 4)
    private BigDecimal productionTonnes;

    @Column(name = "yield_kg_per_ha", precision = 14, scale = 4)
    private BigDecimal yieldKgPerHa;

    @Column(name = "conversion_factor_applied", precision = 8, scale = 4)
    private BigDecimal conversionFactorApplied;

    @Column(name = "conversion_verification_ref", length = 255)
    private String conversionVerificationRef;

    @Column(name = "source_revision", nullable = false, length = 100)
    private String sourceRevision;

    @Column(name = "revision_priority", nullable = false)
    private Integer revisionPriority = 10;

    @Column(name = "file_checksum_sha256", nullable = false, length = 64)
    private String fileChecksumSha256;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "superseded_by_batch_id")
    private Long supersededByBatchId;

    @Column(name = "data_status", nullable = false, length = 30)
    private String dataStatus = "VERIFIED";

    @Column(name = "source_organization", nullable = false, length = 100)
    private String sourceOrganization;

    @Column(name = "source_reference_url", length = 500)
    private String sourceReferenceUrl;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "import_timestamp", nullable = false, updatable = false)
    private LocalDateTime importTimestamp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CropHistoricalProductionObservation() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.importTimestamp == null) {
            this.importTimestamp = now;
        }
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

    public String getSourceCropName() { return sourceCropName; }
    public void setSourceCropName(String sourceCropName) { this.sourceCropName = sourceCropName; }

    public String getSourceState() { return sourceState; }
    public void setSourceState(String sourceState) { this.sourceState = sourceState; }

    public String getSourceDistrict() { return sourceDistrict; }
    public void setSourceDistrict(String sourceDistrict) { this.sourceDistrict = sourceDistrict; }

    public String getNormalizedState() { return normalizedState; }
    public void setNormalizedState(String normalizedState) { this.normalizedState = normalizedState; }

    public String getNormalizedDistrict() { return normalizedDistrict; }
    public void setNormalizedDistrict(String normalizedDistrict) { this.normalizedDistrict = normalizedDistrict; }

    public String getStateLgdCode() { return stateLgdCode; }
    public void setStateLgdCode(String stateLgdCode) { this.stateLgdCode = stateLgdCode; }

    public String getDistrictLgdCode() { return districtLgdCode; }
    public void setDistrictLgdCode(String districtLgdCode) { this.districtLgdCode = districtLgdCode; }

    public String getBoundaryVintage() { return boundaryVintage; }
    public void setBoundaryVintage(String boundaryVintage) { this.boundaryVintage = boundaryVintage; }

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

    public Integer getRevisionPriority() { return revisionPriority; }
    public void setRevisionPriority(Integer revisionPriority) { this.revisionPriority = revisionPriority; }

    public String getFileChecksumSha256() { return fileChecksumSha256; }
    public void setFileChecksumSha256(String fileChecksumSha256) { this.fileChecksumSha256 = fileChecksumSha256; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Long getSupersededByBatchId() { return supersededByBatchId; }
    public void setSupersededByBatchId(Long supersededByBatchId) { this.supersededByBatchId = supersededByBatchId; }

    public String getDataStatus() { return dataStatus; }
    public void setDataStatus(String dataStatus) { this.dataStatus = dataStatus; }

    public String getSourceOrganization() { return sourceOrganization; }
    public void setSourceOrganization(String sourceOrganization) { this.sourceOrganization = sourceOrganization; }

    public String getSourceReferenceUrl() { return sourceReferenceUrl; }
    public void setSourceReferenceUrl(String sourceReferenceUrl) { this.sourceReferenceUrl = sourceReferenceUrl; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public LocalDateTime getImportTimestamp() { return importTimestamp; }
    public void setImportTimestamp(LocalDateTime importTimestamp) { this.importTimestamp = importTimestamp; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
