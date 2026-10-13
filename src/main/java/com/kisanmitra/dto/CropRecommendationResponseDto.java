package com.kisanmitra.dto;

import com.kisanmitra.entity.*;
import java.time.LocalDateTime;
import java.util.Set;

public class CropRecommendationResponseDto {

    private Long id;
    private String pincode;
    private String state;
    private String district;
    private String village;
    private String locality;
    private SoilType soilType;
    private Double soilPh;
    private Boolean soilPhUnknown;
    private WaterAvailability waterAvailability;
    private IrrigationType irrigation;
    private RainfallLevel expectedRainfall;
    private Season season;
    private TemperatureRange temperatureRange;
    private Double farmSize;
    private Set<String> priorities;
    private RecommendationStatus status;
    private LocalDateTime createdAt;
    private String message;

    public CropRecommendationResponseDto() {
    }

    public CropRecommendationResponseDto(Long id, String state, String district, String village, SoilType soilType,
                                         Double soilPh, Boolean soilPhUnknown, WaterAvailability waterAvailability,
                                         IrrigationType irrigation, RainfallLevel expectedRainfall, Season season,
                                         TemperatureRange temperatureRange, Double farmSize, Set<String> priorities,
                                         RecommendationStatus status, LocalDateTime createdAt, String message) {
        this.id = id;
        this.state = state;
        this.district = district;
        this.village = village;
        this.locality = village;
        this.soilType = soilType;
        this.soilPh = soilPh;
        this.soilPhUnknown = soilPhUnknown;
        this.waterAvailability = waterAvailability;
        this.irrigation = irrigation;
        this.expectedRainfall = expectedRainfall;
        this.season = season;
        this.temperatureRange = temperatureRange;
        this.farmSize = farmSize;
        this.priorities = priorities;
        this.status = status;
        this.createdAt = createdAt;
        this.message = message;
    }

    public static CropRecommendationResponseDto fromEntity(CropRecommendationRequest request, String message) {
        if (request == null) {
            return null;
        }
        CropRecommendationResponseDto dto = new CropRecommendationResponseDto(
                request.getId(),
                request.getState(),
                request.getDistrict(),
                request.getVillage(),
                request.getSoilType(),
                request.getSoilPh(),
                request.getSoilPhUnknown(),
                request.getWaterAvailability(),
                request.getIrrigation(),
                request.getExpectedRainfall(),
                request.getSeason(),
                request.getTemperatureRange(),
                request.getFarmSize(),
                request.getPriorities(),
                request.getStatus(),
                request.getCreatedAt(),
                message
        );
        dto.setPincode(request.getPincode());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
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
        if (this.locality == null) {
            this.locality = village;
        }
    }

    public String getLocality() {
        return locality != null ? locality : village;
    }

    public void setLocality(String locality) {
        this.locality = locality;
        if (this.village == null) {
            this.village = locality;
        }
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
