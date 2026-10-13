package com.kisanmitra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "crop_requirements")
public class CropRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Crop reference is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false, unique = true)
    private Crop crop;

    @Column(name = "min_soil_ph")
    private Double minSoilPh;

    @Column(name = "max_soil_ph")
    private Double maxSoilPh;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "crop_requirement_soil_types", joinColumns = @JoinColumn(name = "crop_requirement_id"))
    @Column(name = "soil_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<SoilType> suitableSoilTypes = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "crop_requirement_seasons", joinColumns = @JoinColumn(name = "crop_requirement_id"))
    @Column(name = "season", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Season> suitableSeasons = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "min_rainfall")
    private RainfallLevel minRainfall;

    @Enumerated(EnumType.STRING)
    @Column(name = "max_rainfall")
    private RainfallLevel maxRainfall;

    @Enumerated(EnumType.STRING)
    @Column(name = "min_temperature_range")
    private TemperatureRange minTemperatureRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "max_temperature_range")
    private TemperatureRange maxTemperatureRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "min_water_availability")
    private WaterAvailability minWaterAvailability;

    @Enumerated(EnumType.STRING)
    @Column(name = "max_water_availability")
    private WaterAvailability maxWaterAvailability;

    @Column(name = "irrigation_required", nullable = false)
    private Boolean irrigationRequired = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private CropRiskLevel riskLevel;

    @Column(name = "water_requirement_description", columnDefinition = "TEXT")
    private String waterRequirementDescription;

    @Column(name = "growing_period_description", columnDefinition = "TEXT")
    private String growingPeriodDescription;

    @Column(name = "sustainability_notes", columnDefinition = "TEXT")
    private String sustainabilityNotes;

    @Column(name = "source_name", length = 150)
    private String sourceName;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "source_updated_date")
    private java.time.LocalDate sourceUpdatedDate;

    @NotNull(message = "Active flag is required")
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CropRequirement() {
    }

    public CropRequirement(Crop crop) {
        this.crop = crop;
        this.active = true;
        this.irrigationRequired = false;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
        if (this.active == null) {
            this.active = true;
        }
        if (this.irrigationRequired == null) {
            this.irrigationRequired = false;
        }
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

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public Double getMinSoilPh() {
        return minSoilPh;
    }

    public void setMinSoilPh(Double minSoilPh) {
        this.minSoilPh = minSoilPh;
    }

    public Double getMaxSoilPh() {
        return maxSoilPh;
    }

    public void setMaxSoilPh(Double maxSoilPh) {
        this.maxSoilPh = maxSoilPh;
    }

    public Set<SoilType> getSuitableSoilTypes() {
        return suitableSoilTypes;
    }

    public void setSuitableSoilTypes(Set<SoilType> suitableSoilTypes) {
        this.suitableSoilTypes = suitableSoilTypes;
    }

    public Set<Season> getSuitableSeasons() {
        return suitableSeasons;
    }

    public void setSuitableSeasons(Set<Season> suitableSeasons) {
        this.suitableSeasons = suitableSeasons;
    }

    public RainfallLevel getMinRainfall() {
        return minRainfall;
    }

    public void setMinRainfall(RainfallLevel minRainfall) {
        this.minRainfall = minRainfall;
    }

    public RainfallLevel getMaxRainfall() {
        return maxRainfall;
    }

    public void setMaxRainfall(RainfallLevel maxRainfall) {
        this.maxRainfall = maxRainfall;
    }

    public TemperatureRange getMinTemperatureRange() {
        return minTemperatureRange;
    }

    public void setMinTemperatureRange(TemperatureRange minTemperatureRange) {
        this.minTemperatureRange = minTemperatureRange;
    }

    public TemperatureRange getMaxTemperatureRange() {
        return maxTemperatureRange;
    }

    public void setMaxTemperatureRange(TemperatureRange maxTemperatureRange) {
        this.maxTemperatureRange = maxTemperatureRange;
    }

    public WaterAvailability getMinWaterAvailability() {
        return minWaterAvailability;
    }

    public void setMinWaterAvailability(WaterAvailability minWaterAvailability) {
        this.minWaterAvailability = minWaterAvailability;
    }

    public WaterAvailability getMaxWaterAvailability() {
        return maxWaterAvailability;
    }

    public void setMaxWaterAvailability(WaterAvailability maxWaterAvailability) {
        this.maxWaterAvailability = maxWaterAvailability;
    }

    public Boolean getIrrigationRequired() {
        return irrigationRequired;
    }

    public void setIrrigationRequired(Boolean irrigationRequired) {
        this.irrigationRequired = irrigationRequired;
    }

    public CropRiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(CropRiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getWaterRequirementDescription() {
        return waterRequirementDescription;
    }

    public void setWaterRequirementDescription(String waterRequirementDescription) {
        this.waterRequirementDescription = waterRequirementDescription;
    }

    public String getGrowingPeriodDescription() {
        return growingPeriodDescription;
    }

    public void setGrowingPeriodDescription(String growingPeriodDescription) {
        this.growingPeriodDescription = growingPeriodDescription;
    }

    public String getSustainabilityNotes() {
        return sustainabilityNotes;
    }

    public void setSustainabilityNotes(String sustainabilityNotes) {
        this.sustainabilityNotes = sustainabilityNotes;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public java.time.LocalDate getSourceUpdatedDate() {
        return sourceUpdatedDate;
    }

    public void setSourceUpdatedDate(java.time.LocalDate sourceUpdatedDate) {
        this.sourceUpdatedDate = sourceUpdatedDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
