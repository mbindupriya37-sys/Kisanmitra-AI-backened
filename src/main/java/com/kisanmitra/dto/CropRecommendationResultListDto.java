package com.kisanmitra.dto;

import com.kisanmitra.entity.RecommendationStatus;
import com.kisanmitra.entity.Season;
import com.kisanmitra.entity.SoilType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CropRecommendationResultListDto {

    private Long requestId;
    private RecommendationStatus status;
    private LocalDateTime createdAt;
    private List<CropRecommendationResultDto> recommendations = new ArrayList<>();
    private String disclaimer;

    // Preserved Farm Location & Input Context
    private String pincode;
    private String state;
    private String district;
    private String village;
    private String locality;
    private SoilType soilType;
    private Season season;
    private Double farmSize;

    public CropRecommendationResultListDto() {
    }

    public CropRecommendationResultListDto(Long requestId, RecommendationStatus status, LocalDateTime createdAt,
                                           List<CropRecommendationResultDto> recommendations, String disclaimer) {
        this.requestId = requestId;
        this.status = status;
        this.createdAt = createdAt;
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
        this.disclaimer = disclaimer;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public RecommendationStatus getStatus() {
        return status;
    }

    public void setStatus(RecommendationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CropRecommendationResultDto> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<CropRecommendationResultDto> recommendations) {
        this.recommendations = recommendations;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
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
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public SoilType getSoilType() {
        return soilType;
    }

    public void setSoilType(SoilType soilType) {
        this.soilType = soilType;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Double getFarmSize() {
        return farmSize;
    }

    public void setFarmSize(Double farmSize) {
        this.farmSize = farmSize;
    }
}

