package com.kisanmitra.dto;

public class WeatherCurrentDto {

    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private Double rainfall;
    private Double windSpeed;
    private String weatherCondition;
    private Integer weatherCode;
    private String observationTime;

    public WeatherCurrentDto() {
    }

    public WeatherCurrentDto(Double temperature, Double feelsLike, Integer humidity, Double rainfall,
                             Double windSpeed, String weatherCondition, Integer weatherCode, String observationTime) {
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
        this.weatherCondition = weatherCondition;
        this.weatherCode = weatherCode;
        this.observationTime = observationTime;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(Double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Double getRainfall() {
        return rainfall;
    }

    public void setRainfall(Double rainfall) {
        this.rainfall = rainfall;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
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

    public String getObservationTime() {
        return observationTime;
    }

    public void setObservationTime(String observationTime) {
        this.observationTime = observationTime;
    }
}
