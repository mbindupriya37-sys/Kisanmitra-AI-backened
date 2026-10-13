package com.kisanmitra.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MarketResponseDto {

    private List<String> supportedCrops = new ArrayList<>();
    private String selectedCrop;
    private String selectedState;
    private String selectedDistrict;
    private String selectedMarket;
    private List<MarketObservationDto> observations = new ArrayList<>();
    private Integer totalRecords;
    private List<MarketConsiderationDto> considerations = new ArrayList<>();
    private String disclaimer;
    private String sourceAttribution;
    private LocalDateTime fetchedAt;

    public MarketResponseDto() {
    }

    public MarketResponseDto(List<String> supportedCrops, String selectedCrop, String selectedState,
                             String selectedDistrict, String selectedMarket,
                             List<MarketObservationDto> observations, Integer totalRecords,
                             List<MarketConsiderationDto> considerations, String disclaimer,
                             String sourceAttribution, LocalDateTime fetchedAt) {
        this.supportedCrops = supportedCrops != null ? supportedCrops : new ArrayList<>();
        this.selectedCrop = selectedCrop;
        this.selectedState = selectedState;
        this.selectedDistrict = selectedDistrict;
        this.selectedMarket = selectedMarket;
        this.observations = observations != null ? observations : new ArrayList<>();
        this.totalRecords = totalRecords;
        this.considerations = considerations != null ? considerations : new ArrayList<>();
        this.disclaimer = disclaimer;
        this.sourceAttribution = sourceAttribution;
        this.fetchedAt = fetchedAt;
    }

    public List<String> getSupportedCrops() {
        return supportedCrops;
    }

    public void setSupportedCrops(List<String> supportedCrops) {
        this.supportedCrops = supportedCrops;
    }

    public String getSelectedCrop() {
        return selectedCrop;
    }

    public void setSelectedCrop(String selectedCrop) {
        this.selectedCrop = selectedCrop;
    }

    public String getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(String selectedState) {
        this.selectedState = selectedState;
    }

    public String getSelectedDistrict() {
        return selectedDistrict;
    }

    public void setSelectedDistrict(String selectedDistrict) {
        this.selectedDistrict = selectedDistrict;
    }

    public String getSelectedMarket() {
        return selectedMarket;
    }

    public void setSelectedMarket(String selectedMarket) {
        this.selectedMarket = selectedMarket;
    }

    public List<MarketObservationDto> getObservations() {
        return observations;
    }

    public void setObservations(List<MarketObservationDto> observations) {
        this.observations = observations;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<MarketConsiderationDto> getConsiderations() {
        return considerations;
    }

    public void setConsiderations(List<MarketConsiderationDto> considerations) {
        this.considerations = considerations;
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
