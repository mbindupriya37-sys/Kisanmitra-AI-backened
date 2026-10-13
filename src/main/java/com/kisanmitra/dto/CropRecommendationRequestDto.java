package com.kisanmitra.dto;

import com.kisanmitra.dto.validation.ValidSoilPh;
import com.kisanmitra.entity.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@ValidSoilPh
public class CropRecommendationRequestDto {

    @Pattern(regexp = "^$|^[1-9][0-9]{5}$", message = "PIN code must be a valid 6-digit Indian postal code")
    private String pincode;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State name cannot exceed 100 characters")
    private String state;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District name cannot exceed 100 characters")
    private String district;

    @Size(max = 150, message = "Village name cannot exceed 150 characters")
    private String village;

    @Size(max = 150, message = "Locality name cannot exceed 150 characters")
    private String locality;

    @NotNull(message = "Please select a soil type")
    private SoilType soilType;

    private Double soilPh;

    private Boolean soilPhUnknown = false;

    @NotNull(message = "Please select water availability")
    private WaterAvailability waterAvailability;

    @NotNull(message = "Please specify irrigation availability")
    private IrrigationType irrigation;

    @NotNull(message = "Please select expected rainfall")
    private RainfallLevel expectedRainfall;

    @NotNull(message = "Please select a season")
    private Season season;

    private TemperatureRange temperatureRange;

    @NotNull(message = "Farm size is required")
    @Positive(message = "Farm size must be greater than 0 acres")
    private Double farmSize;

    @Size(max = 10, message = "You can select at most 10 priorities")
    private Set<String> priorities = new HashSet<>();

    public CropRecommendationRequestDto() {
    }

    public CropRecommendationRequestDto(String state, String district, String village, SoilType soilType,
                                        Double soilPh, Boolean soilPhUnknown, WaterAvailability waterAvailability,
                                        IrrigationType irrigation, RainfallLevel expectedRainfall, Season season,
                                        TemperatureRange temperatureRange, Double farmSize, Set<String> priorities) {
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
        return village != null && !village.isBlank() ? village : locality;
    }

    public void setVillage(String village) {
        this.village = village;
        if (this.locality == null || this.locality.isBlank()) {
            this.locality = village;
        }
    }

    public String getLocality() {
        return locality != null && !locality.isBlank() ? locality : village;
    }

    public void setLocality(String locality) {
        this.locality = locality;
        if (this.village == null || this.village.isBlank()) {
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
}
