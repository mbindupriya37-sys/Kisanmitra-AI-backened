package com.kisanmitra.dto;

import com.kisanmitra.entity.FarmerCropIntention;
import com.kisanmitra.entity.Season;
import java.time.LocalDateTime;

public class FarmerCropIntentionResponseDto {

    private Long id;
    private CropSummaryDto crop;
    private String state;
    private String district;
    private Season intendedSeason;
    private Double farmSize;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FarmerCropIntentionResponseDto() {
    }

    public FarmerCropIntentionResponseDto(Long id, CropSummaryDto crop, String state, String district,
                                          Season intendedSeason, Double farmSize,
                                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.crop = crop;
        this.state = state;
        this.district = district;
        this.intendedSeason = intendedSeason;
        this.farmSize = farmSize;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static FarmerCropIntentionResponseDto fromEntity(FarmerCropIntention entity) {
        if (entity == null) return null;
        CropSummaryDto cropSummary = entity.getCrop() != null
                ? CropSummaryDto.fromEntity(entity.getCrop())
                : null;

        return new FarmerCropIntentionResponseDto(
                entity.getId(),
                cropSummary,
                entity.getState(),
                entity.getDistrict(),
                entity.getIntendedSeason(),
                entity.getFarmSize(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CropSummaryDto getCrop() {
        return crop;
    }

    public void setCrop(CropSummaryDto crop) {
        this.crop = crop;
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

    public Season getIntendedSeason() {
        return intendedSeason;
    }

    public void setIntendedSeason(Season intendedSeason) {
        this.intendedSeason = intendedSeason;
    }

    public Double getFarmSize() {
        return farmSize;
    }

    public void setFarmSize(Double farmSize) {
        this.farmSize = farmSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
