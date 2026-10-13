package com.kisanmitra.dto;

import com.kisanmitra.entity.Season;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FarmerCropIntentionRequestDto {

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "District is required")
    private String district;

    @NotNull(message = "Crop ID is required")
    private Long cropId;

    @NotNull(message = "Intended season is required")
    private Season intendedSeason;

    @NotNull(message = "Farm size is required")
    @Positive(message = "Farm size must be a positive number (> 0)")
    private Double farmSize;

    public FarmerCropIntentionRequestDto() {
    }

    public FarmerCropIntentionRequestDto(String state, String district, Long cropId, Season intendedSeason, Double farmSize) {
        this.state = state;
        this.district = district;
        this.cropId = cropId;
        this.intendedSeason = intendedSeason;
        this.farmSize = farmSize;
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

    public Long getCropId() {
        return cropId;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }

    public Season getIntendedSeason() {
        return intendedSeason;
    }

    public void setIntendedSeason(Season intendedSeason) {
        this.intendedSeason = intendedSeason;
    }

    public Double getFarmSize() {
        return farmSize;
    }

    public void setFarmSize(Double farmSize) {
        this.farmSize = farmSize;
    }
}
