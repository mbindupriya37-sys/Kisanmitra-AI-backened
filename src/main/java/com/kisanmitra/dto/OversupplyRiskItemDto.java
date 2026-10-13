package com.kisanmitra.dto;

import com.kisanmitra.entity.Season;

public class OversupplyRiskItemDto {

    private Long cropId;
    private String cropName;
    private String scientificName;
    private String state;
    private String district;
    private Season season;
    private Integer farmerIntentionCount;
    private Double intendedArea;
    private Double intentionShare;
    private Double areaShare;
    private String riskLevel; // INSUFFICIENT_DATA, LOW, MODERATE, HIGH
    private String riskLabel;
    private String explanation;
    private Boolean dataSufficient;

    public OversupplyRiskItemDto() {
    }

    public OversupplyRiskItemDto(Long cropId, String cropName, String scientificName, String state, String district,
                                Season season, Integer farmerIntentionCount, Double intendedArea,
                                Double intentionShare, Double areaShare, String riskLevel, String riskLabel,
                                String explanation, Boolean dataSufficient) {
        this.cropId = cropId;
        this.cropName = cropName;
        this.scientificName = scientificName;
        this.state = state;
        this.district = district;
        this.season = season;
        this.farmerIntentionCount = farmerIntentionCount;
        this.intendedArea = intendedArea;
        this.intentionShare = intentionShare;
        this.areaShare = areaShare;
        this.riskLevel = riskLevel;
        this.riskLabel = riskLabel;
        this.explanation = explanation;
        this.dataSufficient = dataSufficient;
    }

    public Long getCropId() {
        return cropId;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
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

    public Integer getFarmerIntentionCount() {
        return farmerIntentionCount;
    }

    public void setFarmerIntentionCount(Integer farmerIntentionCount) {
        this.farmerIntentionCount = farmerIntentionCount;
    }

    public Double getIntendedArea() {
        return intendedArea;
    }

    public void setIntendedArea(Double intendedArea) {
        this.intendedArea = intendedArea;
    }

    public Double getIntentionShare() {
        return intentionShare;
    }

    public void setIntentionShare(Double intentionShare) {
        this.intentionShare = intentionShare;
    }

    public Double getAreaShare() {
        return areaShare;
    }

    public void setAreaShare(Double areaShare) {
        this.areaShare = areaShare;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskLabel() {
        return riskLabel;
    }

    public void setRiskLabel(String riskLabel) {
        this.riskLabel = riskLabel;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Boolean getDataSufficient() {
        return dataSufficient;
    }

    public void setDataSufficient(Boolean dataSufficient) {
        this.dataSufficient = dataSufficient;
    }
}
