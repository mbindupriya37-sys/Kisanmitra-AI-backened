package com.kisanmitra.dto;

public class WeatherRiskDto {

    private String riskType;
    private String title;
    private String level; // LOW, MODERATE, HIGH
    private String reason;
    private String timeframe;

    public WeatherRiskDto() {
    }

    public WeatherRiskDto(String riskType, String title, String level, String reason, String timeframe) {
        this.riskType = riskType;
        this.title = title;
        this.level = level;
        this.reason = reason;
        this.timeframe = timeframe;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
}
