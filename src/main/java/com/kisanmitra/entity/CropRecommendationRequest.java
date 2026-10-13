package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "crop_recommendation_requests")
public class CropRecommendationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private User farmer;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String district;

    private String village;

    @Column(length = 10)
    private String pincode;

    @Enumerated(EnumType.STRING)
    @Column(name = "soil_type", nullable = false)
    private SoilType soilType;

    @Column(name = "soil_ph")
    private Double soilPh;

    @Column(name = "soil_ph_unknown", nullable = false)
    private Boolean soilPhUnknown = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "water_availability", nullable = false)
    private WaterAvailability waterAvailability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IrrigationType irrigation;

    @Enumerated(EnumType.STRING)
    @Column(name = "expected_rainfall", nullable = false)
    private RainfallLevel expectedRainfall;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperature_range")
    private TemperatureRange temperatureRange;

    @Column(name = "farm_size", nullable = false)
    private Double farmSize;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "crop_recommendation_priorities", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "priority")
    private Set<String> priorities = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationStatus status = RecommendationStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CropRecommendationRequest() {
    }

    public CropRecommendationRequest(Long id, User farmer, String state, String district, String village,
                                     SoilType soilType, Double soilPh, Boolean soilPhUnknown,
                                     WaterAvailability waterAvailability, IrrigationType irrigation,
                                     RainfallLevel expectedRainfall, Season season, TemperatureRange temperatureRange,
                                     Double farmSize, Set<String> priorities, RecommendationStatus status,
                                     LocalDateTime createdAt) {
        this.id = id;
        this.farmer = farmer;
        this.state = state;
        this.district = district;
        this.village = village;
        this.soilType = soilType;
        this.soilPh = soilPh;
        this.soilPhUnknown = soilPhUnknown != null ? soilPhUnknown : false;
        this.waterAvailability = waterAvailability;
        this.irrigation = irrigation;
        this.expectedRainfall = expectedRainfall;
        this.season = season;
        this.temperatureRange = temperatureRange;
        this.farmSize = farmSize;
        if (priorities != null) {
            this.priorities = priorities;
        }
        this.status = status != null ? status : RecommendationStatus.PENDING;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = RecommendationStatus.PENDING;
        }
        if (this.soilPhUnknown == null) {
            this.soilPhUnknown = false;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFarmer() {
        return farmer;
    }

    public void setFarmer(User farmer) {
        this.farmer = farmer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public SoilType getSoilType() {
        return soilType;
    }

    public void setSoilType(SoilType soilType) {
        this.soilType = soilType;
    }

    public Double getSoilPh() {
        return soilPh;
    }

    public void setSoilPh(Double soilPh) {
        this.soilPh = soilPh;
    }

    public Boolean getSoilPhUnknown() {
        return soilPhUnknown;
    }

    public void setSoilPhUnknown(Boolean soilPhUnknown) {
        this.soilPhUnknown = soilPhUnknown;
    }

    public WaterAvailability getWaterAvailability() {
        return waterAvailability;
    }

    public void setWaterAvailability(WaterAvailability waterAvailability) {
        this.waterAvailability = waterAvailability;
    }

    public IrrigationType getIrrigation() {
        return irrigation;
    }

    public void setIrrigation(IrrigationType irrigation) {
        this.irrigation = irrigation;
    }

    public RainfallLevel getExpectedRainfall() {
        return expectedRainfall;
    }

    public void setExpectedRainfall(RainfallLevel expectedRainfall) {
        this.expectedRainfall = expectedRainfall;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public TemperatureRange getTemperatureRange() {
        return temperatureRange;
    }

    public void setTemperatureRange(TemperatureRange temperatureRange) {
        this.temperatureRange = temperatureRange;
    }

    public Double getFarmSize() {
        return farmSize;
    }

    public void setFarmSize(Double farmSize) {
        this.farmSize = farmSize;
    }

    public Set<String> getPriorities() {
        return priorities;
    }

    public void setPriorities(Set<String> priorities) {
        this.priorities = priorities;
    }

    public RecommendationStatus getStatus() {
        return status;
    }

    public void setStatus(RecommendationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
