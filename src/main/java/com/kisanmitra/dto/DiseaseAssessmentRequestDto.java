package com.kisanmitra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class DiseaseAssessmentRequestDto {

    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotBlank(message = "Affected plant part is required")
    private String affectedPart;

    @Min(value = 1, message = "Symptom duration must be at least 1 day")
    private Integer symptomDurationDays;

    @NotBlank(message = "Severity level is required (MILD, MODERATE, SEVERE)")
    private String severityLevel;

    @NotBlank(message = "Symptom description is required")
    private String symptomsDescription;

    public DiseaseAssessmentRequestDto() {
    }

    public DiseaseAssessmentRequestDto(String cropName, String affectedPart, Integer symptomDurationDays,
                                        String severityLevel, String symptomsDescription) {
        this.cropName = cropName;
        this.affectedPart = affectedPart;
        this.symptomDurationDays = symptomDurationDays;
        this.severityLevel = severityLevel;
        this.symptomsDescription = symptomsDescription;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getAffectedPart() {
        return affectedPart;
    }

    public void setAffectedPart(String affectedPart) {
        this.affectedPart = affectedPart;
    }

    public Integer getSymptomDurationDays() {
        return symptomDurationDays;
    }

    public void setSymptomDurationDays(Integer symptomDurationDays) {
        this.symptomDurationDays = symptomDurationDays;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getSymptomsDescription() {
        return symptomsDescription;
    }

    public void setSymptomsDescription(String symptomsDescription) {
        this.symptomsDescription = symptomsDescription;
    }
}
