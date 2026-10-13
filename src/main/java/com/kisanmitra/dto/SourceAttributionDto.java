package com.kisanmitra.dto;

import java.time.LocalDate;

public class SourceAttributionDto {

    private String name;
    private String url;
    private LocalDate updatedDate;

    public SourceAttributionDto() {
    }

    public SourceAttributionDto(String name, String url, LocalDate updatedDate) {
        this.name = name;
        this.url = url;
        this.updatedDate = updatedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }
}
