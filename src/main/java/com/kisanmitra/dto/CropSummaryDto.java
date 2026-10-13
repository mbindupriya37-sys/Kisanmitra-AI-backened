package com.kisanmitra.dto;

import com.kisanmitra.entity.Crop;

public class CropSummaryDto {

    private Long id;
    private String name;
    private String scientificName;
    private String description;

    public CropSummaryDto() {
    }

    public CropSummaryDto(Long id, String name, String scientificName, String description) {
        this.id = id;
        this.name = name;
        this.scientificName = scientificName;
        this.description = description;
    }

    public static CropSummaryDto fromEntity(Crop crop) {
        if (crop == null) return null;
        return new CropSummaryDto(crop.getId(), crop.getName(), crop.getScientificName(), crop.getDescription());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
