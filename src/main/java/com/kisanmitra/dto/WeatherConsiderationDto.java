package com.kisanmitra.dto;

public class WeatherConsiderationDto {

    private String category;
    private String title;
    private String advice;
    private String icon;

    public WeatherConsiderationDto() {
    }

    public WeatherConsiderationDto(String category, String title, String advice, String icon) {
        this.category = category;
        this.title = title;
        this.advice = advice;
        this.icon = icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
