package com.kisanmitra.dto;

import com.kisanmitra.entity.Season;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OversupplyRiskResponseDto {

    private String state;
    private String district;
    private Season season;
    private Integer totalRegionalIntentions;
    private Integer totalDistinctFarmers;
    private Double totalRegionalArea;
    private Boolean dataSufficient;
    private String summaryMessage;
    private List<OversupplyRiskItemDto> cropRiskItems = new ArrayList<>();
    private LocalDateTime timestamp;

    public OversupplyRiskResponseDto() {
    }

    public OversupplyRiskResponseDto(String state, String district, Season season, Integer totalRegionalIntentions,
                                     Integer totalDistinctFarmers, Double totalRegionalArea, Boolean dataSufficient,
                                     String summaryMessage, List<OversupplyRiskItemDto> cropRiskItems, LocalDateTime timestamp) {
        this.state = state;
        this.district = district;
        this.season = season;
        this.totalRegionalIntentions = totalRegionalIntentions;
        this.totalDistinctFarmers = totalDistinctFarmers;
        this.totalRegionalArea = totalRegionalArea;
        this.dataSufficient = dataSufficient;
        this.summaryMessage = summaryMessage;
        this.cropRiskItems = cropRiskItems != null ? cropRiskItems : new ArrayList<>();
        this.timestamp = timestamp;
    }

    public OversupplyRiskResponseDto(String state, String district, Season season, Integer totalRegionalIntentions,
                                     Double totalRegionalArea, Boolean dataSufficient, String summaryMessage,
                                     List<OversupplyRiskItemDto> cropRiskItems, LocalDateTime timestamp) {
        this(state, district, season, totalRegionalIntentions, 0, totalRegionalArea, dataSufficient, summaryMessage, cropRiskItems, timestamp);
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

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Integer getTotalRegionalIntentions() {
        return totalRegionalIntentions;
    }

    public void setTotalRegionalIntentions(Integer totalRegionalIntentions) {
        this.totalRegionalIntentions = totalRegionalIntentions;
    }

    public Integer getTotalDistinctFarmers() {
        return totalDistinctFarmers;
    }

    public void setTotalDistinctFarmers(Integer totalDistinctFarmers) {
        this.totalDistinctFarmers = totalDistinctFarmers;
    }

    public Double getTotalRegionalArea() {
        return totalRegionalArea;
    }

    public void setTotalRegionalArea(Double totalRegionalArea) {
        this.totalRegionalArea = totalRegionalArea;
    }

    public Boolean getDataSufficient() {
        return dataSufficient;
    }

    public void setDataSufficient(Boolean dataSufficient) {
        this.dataSufficient = dataSufficient;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public void setSummaryMessage(String summaryMessage) {
        this.summaryMessage = summaryMessage;
    }

    public List<OversupplyRiskItemDto> getCropRiskItems() {
        return cropRiskItems;
    }

    public void setCropRiskItems(List<OversupplyRiskItemDto> cropRiskItems) {
        this.cropRiskItems = cropRiskItems;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
