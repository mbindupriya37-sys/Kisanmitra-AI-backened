package com.kisanmitra.dto;

public class WeatherLocationDto {

    private String state;
    private String district;
    private Double latitude;
    private Double longitude;
    private String regionName;

    public WeatherLocationDto() {
    }

    public WeatherLocationDto(String state, String district, Double latitude, Double longitude, String regionName) {
        this.state = state;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.regionName = regionName;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
