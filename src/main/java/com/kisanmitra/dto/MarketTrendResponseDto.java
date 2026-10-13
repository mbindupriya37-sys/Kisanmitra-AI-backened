package com.kisanmitra.dto;

import java.util.ArrayList;
import java.util.List;

public class MarketTrendResponseDto {

    private String cropName;
    private String marketName;
    private String state;
    private String unit;
    private Boolean hasSufficientHistory;
    private String trendDirection; // UPWARD, DOWNWARD, STABLE, INSUFFICIENT_DATA
    private List<MarketTrendPointDto> trendPoints = new ArrayList<>();
    private String explanation;

    public MarketTrendResponseDto() {
    }

    public MarketTrendResponseDto(String cropName, String marketName, String state, String unit,
                                  Boolean hasSufficientHistory, String trendDirection,
                                  List<MarketTrendPointDto> trendPoints, String explanation) {
        this.cropName = cropName;
        this.marketName = marketName;
        this.state = state;
        this.unit = unit;
        this.hasSufficientHistory = hasSufficientHistory;
        this.trendDirection = trendDirection;
        this.trendPoints = trendPoints != null ? trendPoints : new ArrayList<>();
        this.explanation = explanation;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getHasSufficientHistory() {
        return hasSufficientHistory;
    }

    public void setHasSufficientHistory(Boolean hasSufficientHistory) {
        this.hasSufficientHistory = hasSufficientHistory;
    }

    public String getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(String trendDirection) {
        this.trendDirection = trendDirection;
    }

    public List<MarketTrendPointDto> getTrendPoints() {
        return trendPoints;
    }

    public void setTrendPoints(List<MarketTrendPointDto> trendPoints) {
        this.trendPoints = trendPoints;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
