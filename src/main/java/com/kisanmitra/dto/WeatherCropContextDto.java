package com.kisanmitra.dto;

import java.util.ArrayList;
import java.util.List;

public class WeatherCropContextDto {

    private Long cropId;
    private String cropName;
    private Boolean available;
    private String compatibilityStatus; // SUITABLE, ATTENTION_REQUIRED, UNFAVORABLE, NOT_AVAILABLE
    private String summary;
    private List<String> details = new ArrayList<>();

    public WeatherCropContextDto() {
    }

    public WeatherCropContextDto(Long cropId, String cropName, Boolean available, String compatibilityStatus,
                                 String summary, List<String> details) {
        this.cropId = cropId;
        this.cropName = cropName;
        this.available = available;
        this.compatibilityStatus = compatibilityStatus;
        this.summary = summary;
        this.details = details != null ? details : new ArrayList<>();
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

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getCompatibilityStatus() {
        return compatibilityStatus;
    }

    public void setCompatibilityStatus(String compatibilityStatus) {
        this.compatibilityStatus = compatibilityStatus;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
