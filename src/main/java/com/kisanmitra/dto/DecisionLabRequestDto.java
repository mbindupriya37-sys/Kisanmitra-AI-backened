package com.kisanmitra.dto;

import com.kisanmitra.entity.*;
import jakarta.validation.constraints.NotNull;

public class DecisionLabRequestDto {

    @NotNull(message = "Request ID is required")
    private Long requestId;

    private Double soilPh;
    private Boolean soilPhUnknown;

    @NotNull(message = "Water availability is required")
    private WaterAvailability waterAvailability;

    @NotNull(message = "Expected rainfall is required")
    private RainfallLevel expectedRainfall;

    @NotNull(message = "Target season is required")
    private Season season;

    private TemperatureRange temperatureRange;

    @NotNull(message = "Irrigation status is required")
    private IrrigationType irrigation;

    public DecisionLabRequestDto() {
    }

    public DecisionLabRequestDto(Long requestId, Double soilPh, Boolean soilPhUnknown,
                                WaterAvailability waterAvailability, RainfallLevel expectedRainfall,
                                Season season, TemperatureRange temperatureRange, IrrigationType irrigation) {
        this.requestId = requestId;
        this.soilPh = soilPh;
        this.soilPhUnknown = soilPhUnknown;
        this.waterAvailability = waterAvailability;
        this.expectedRainfall = expectedRainfall;
        this.season = season;
        this.temperatureRange = temperatureRange;
        this.irrigation = irrigation;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public IrrigationType getIrrigation() {
        return irrigation;
    }

    public void setIrrigation(IrrigationType irrigation) {
        this.irrigation = irrigation;
    }
}
