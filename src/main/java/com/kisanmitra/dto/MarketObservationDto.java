package com.kisanmitra.dto;

public class MarketObservationDto {

    private String id;
    private String cropName;
    private String state;
    private String district;
    private String marketName;
    private String variety;
    private String observationDate;
    private Double minPrice;
    private Double maxPrice;
    private Double modalPrice;
    private String currency;
    private String unit;
    private String sourceName;
    private String sourceUrl;
    private String freshnessStatus;
    private String retrievedAt;

    public MarketObservationDto() {
    }

    public MarketObservationDto(String id, String cropName, String state, String district, String marketName,
                                String variety, String observationDate, Double minPrice, Double maxPrice,
                                Double modalPrice, String currency, String unit, String sourceName,
                                String sourceUrl, String freshnessStatus) {
        this(id, cropName, state, district, marketName, variety, observationDate, minPrice, maxPrice, modalPrice, currency, unit, sourceName, sourceUrl, freshnessStatus, java.time.LocalDateTime.now().toString());
    }

    public MarketObservationDto(String id, String cropName, String state, String district, String marketName,
                                String variety, String observationDate, Double minPrice, Double maxPrice,
                                Double modalPrice, String currency, String unit, String sourceName,
                                String sourceUrl, String freshnessStatus, String retrievedAt) {
        this.id = id;
        this.cropName = cropName;
        this.state = state;
        this.district = district;
        this.marketName = marketName;
        this.variety = variety;
        this.observationDate = observationDate;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.modalPrice = modalPrice;
        this.currency = currency;
        this.unit = unit;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.freshnessStatus = freshnessStatus;
        this.retrievedAt = retrievedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(String observationDate) {
        this.observationDate = observationDate;
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

    public Double getModalPrice() {
        return modalPrice;
    }

    public void setModalPrice(Double modalPrice) {
        this.modalPrice = modalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getFreshnessStatus() {
        return freshnessStatus;
    }

    public void setFreshnessStatus(String freshnessStatus) {
        this.freshnessStatus = freshnessStatus;
    }

    public String getRetrievedAt() {
        return retrievedAt;
    }

    public void setRetrievedAt(String retrievedAt) {
        this.retrievedAt = retrievedAt;
    }
}
