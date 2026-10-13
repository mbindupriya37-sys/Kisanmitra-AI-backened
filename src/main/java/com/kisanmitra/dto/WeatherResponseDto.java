package com.kisanmitra.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WeatherResponseDto {

    private WeatherLocationDto location;
    private WeatherCurrentDto current;
    private List<WeatherForecastItemDto> forecast = new ArrayList<>();
    private List<WeatherRiskDto> risks = new ArrayList<>();
    private List<WeatherConsiderationDto> considerations = new ArrayList<>();
    private WeatherCropContextDto cropContext;
    private String disclaimer;
    private String sourceAttribution;
    private LocalDateTime fetchedAt;

    public WeatherResponseDto() {
    }

    public WeatherResponseDto(WeatherLocationDto location, WeatherCurrentDto current,
                              List<WeatherForecastItemDto> forecast, List<WeatherRiskDto> risks,
                              List<WeatherConsiderationDto> considerations, WeatherCropContextDto cropContext,
                              String disclaimer, String sourceAttribution, LocalDateTime fetchedAt) {
        this.location = location;
        this.current = current;
        this.forecast = forecast != null ? forecast : new ArrayList<>();
        this.risks = risks != null ? risks : new ArrayList<>();
        this.considerations = considerations != null ? considerations : new ArrayList<>();
        this.cropContext = cropContext;
        this.disclaimer = disclaimer;
        this.sourceAttribution = sourceAttribution;
        this.fetchedAt = fetchedAt;
    }

    public WeatherLocationDto getLocation() {
        return location;
    }

    public void setLocation(WeatherLocationDto location) {
        this.location = location;
    }

    public WeatherCurrentDto getCurrent() {
        return current;
    }

    public void setCurrent(WeatherCurrentDto current) {
        this.current = current;
    }

    public List<WeatherForecastItemDto> getForecast() {
        return forecast;
    }

    public void setForecast(List<WeatherForecastItemDto> forecast) {
        this.forecast = forecast;
    }

    public List<WeatherRiskDto> getRisks() {
        return risks;
    }

    public void setRisks(List<WeatherRiskDto> risks) {
        this.risks = risks;
    }

    public List<WeatherConsiderationDto> getConsiderations() {
        return considerations;
    }

    public void setConsiderations(List<WeatherConsiderationDto> considerations) {
        this.considerations = considerations;
    }

    public WeatherCropContextDto getCropContext() {
        return cropContext;
    }

    public void setCropContext(WeatherCropContextDto cropContext) {
        this.cropContext = cropContext;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getSourceAttribution() {
        return sourceAttribution;
    }

    public void setSourceAttribution(String sourceAttribution) {
        this.sourceAttribution = sourceAttribution;
    }

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
