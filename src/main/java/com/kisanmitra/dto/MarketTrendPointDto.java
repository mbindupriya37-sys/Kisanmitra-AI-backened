package com.kisanmitra.dto;

public class MarketTrendPointDto {

    private String date;
    private Double modalPrice;
    private Double minPrice;
    private Double maxPrice;
    private String marketName;
    private String unit;

    public MarketTrendPointDto() {
    }

    public MarketTrendPointDto(String date, Double modalPrice, Double minPrice, Double maxPrice, String marketName, String unit) {
        this.date = date;
        this.modalPrice = modalPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.marketName = marketName;
        this.unit = unit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getModalPrice() {
        return modalPrice;
    }

    public void setModalPrice(Double modalPrice) {
        this.modalPrice = modalPrice;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
