package com.kisanmitra.dto;

public class WeatherForecastItemDto {

    private String date;
    private String dayOfWeek;
    private Double minTemperature;
    private Double maxTemperature;
    private Integer precipitationProbability;
    private Double precipitationSum;
    private String weatherCondition;
    private Integer weatherCode;

    public WeatherForecastItemDto() {
    }

    public WeatherForecastItemDto(String date, String dayOfWeek, Double minTemperature, Double maxTemperature,
                                  Integer precipitationProbability, Double precipitationSum,
                                  String weatherCondition, Integer weatherCode) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.precipitationProbability = precipitationProbability;
        this.precipitationSum = precipitationSum;
        this.weatherCondition = weatherCondition;
        this.weatherCode = weatherCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Integer getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(Integer precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    public Double getPrecipitationSum() {
        return precipitationSum;
    }

    public void setPrecipitationSum(Double precipitationSum) {
        this.precipitationSum = precipitationSum;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public Integer getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(Integer weatherCode) {
        this.weatherCode = weatherCode;
    }
}
