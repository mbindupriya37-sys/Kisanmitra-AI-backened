package com.kisanmitra.dto;

import java.util.ArrayList;
import java.util.List;

public class MarketComparisonResponseDto {

    private String cropName;
    private String state;
    private Double averageModalPrice;
    private String unit;
    private List<MarketComparisonItemDto> comparisonItems = new ArrayList<>();
    private String summaryExplanation;
    private String disclaimer;

    public MarketComparisonResponseDto() {
    }

    public MarketComparisonResponseDto(String cropName, String state, Double averageModalPrice, String unit,
                                       List<MarketComparisonItemDto> comparisonItems, String summaryExplanation,
                                       String disclaimer) {
        this.cropName = cropName;
        this.state = state;
        this.averageModalPrice = averageModalPrice;
        this.unit = unit;
        this.comparisonItems = comparisonItems != null ? comparisonItems : new ArrayList<>();
        this.summaryExplanation = summaryExplanation;
        this.disclaimer = disclaimer;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getAverageModalPrice() {
        return averageModalPrice;
    }

    public void setAverageModalPrice(Double averageModalPrice) {
        this.averageModalPrice = averageModalPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<MarketComparisonItemDto> getComparisonItems() {
        return comparisonItems;
    }

    public void setComparisonItems(List<MarketComparisonItemDto> comparisonItems) {
        this.comparisonItems = comparisonItems;
    }

    public String getSummaryExplanation() {
        return summaryExplanation;
    }

    public void setSummaryExplanation(String summaryExplanation) {
        this.summaryExplanation = summaryExplanation;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
