package com.kisanmitra.dto;

public class MarketComparisonItemDto {

    private String marketName;
    private String district;
    private String state;
    private Double modalPrice;
    private String unit;
    private String observationDate;
    private Double priceDifference;
    private Boolean isHighestPrice;
    private Boolean isLowestPrice;
    private String freshness;

    public MarketComparisonItemDto() {
    }

    public MarketComparisonItemDto(String marketName, String district, String state, Double modalPrice,
                                   String unit, String observationDate, Double priceDifference,
                                   Boolean isHighestPrice, Boolean isLowestPrice, String freshness) {
        this.marketName = marketName;
        this.district = district;
        this.state = state;
        this.modalPrice = modalPrice;
        this.unit = unit;
        this.observationDate = observationDate;
        this.priceDifference = priceDifference;
        this.isHighestPrice = isHighestPrice;
        this.isLowestPrice = isLowestPrice;
        this.freshness = freshness;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getModalPrice() {
        return modalPrice;
    }

    public void setModalPrice(Double modalPrice) {
        this.modalPrice = modalPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(String observationDate) {
        this.observationDate = observationDate;
    }

    public Double getPriceDifference() {
        return priceDifference;
    }

    public void setPriceDifference(Double priceDifference) {
        this.priceDifference = priceDifference;
    }

    public Boolean getIsHighestPrice() {
        return isHighestPrice;
    }

    public void setIsHighestPrice(Boolean isHighestPrice) {
        this.isHighestPrice = isHighestPrice;
    }

    public Boolean getIsLowestPrice() {
        return isLowestPrice;
    }

    public void setIsLowestPrice(Boolean isLowestPrice) {
        this.isLowestPrice = isLowestPrice;
    }

    public String getFreshness() {
        return freshness;
    }

    public void setFreshness(String freshness) {
        this.freshness = freshness;
    }
}
