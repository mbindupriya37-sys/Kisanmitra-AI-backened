package com.kisanmitra.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiseaseAssessmentResponseDto {

    private String submittedCrop;
    private String submittedAffectedPart;
    private String submittedSeverity;
    private Integer submittedDurationDays;
    private List<CropDiseaseKnowledgeDto> possibleCauses = new ArrayList<>();
    private Boolean isUrgentKvkConsultationRecommended;
    private String kvkEscalationReason;
    private String diagnosticDisclaimer;
    private String chemicalAdvisoryNotice;
    private LocalDateTime assessedAt;

    public DiseaseAssessmentResponseDto() {
    }

    public DiseaseAssessmentResponseDto(String submittedCrop, String submittedAffectedPart,
                                        String submittedSeverity, Integer submittedDurationDays,
                                        List<CropDiseaseKnowledgeDto> possibleCauses,
                                        Boolean isUrgentKvkConsultationRecommended,
                                        String kvkEscalationReason, String diagnosticDisclaimer,
                                        String chemicalAdvisoryNotice, LocalDateTime assessedAt) {
        this.submittedCrop = submittedCrop;
        this.submittedAffectedPart = submittedAffectedPart;
        this.submittedSeverity = submittedSeverity;
        this.submittedDurationDays = submittedDurationDays;
        this.possibleCauses = possibleCauses != null ? possibleCauses : new ArrayList<>();
        this.isUrgentKvkConsultationRecommended = isUrgentKvkConsultationRecommended;
        this.kvkEscalationReason = kvkEscalationReason;
        this.diagnosticDisclaimer = diagnosticDisclaimer;
        this.chemicalAdvisoryNotice = chemicalAdvisoryNotice;
        this.assessedAt = assessedAt;
    }

    public String getSubmittedCrop() {
        return submittedCrop;
    }

    public void setSubmittedCrop(String submittedCrop) {
        this.submittedCrop = submittedCrop;
    }

    public String getSubmittedAffectedPart() {
        return submittedAffectedPart;
    }

    public void setSubmittedAffectedPart(String submittedAffectedPart) {
        this.submittedAffectedPart = submittedAffectedPart;
    }

    public String getSubmittedSeverity() {
        return submittedSeverity;
    }

    public void setSubmittedSeverity(String submittedSeverity) {
        this.submittedSeverity = submittedSeverity;
    }

    public Integer getSubmittedDurationDays() {
        return submittedDurationDays;
    }

    public void setSubmittedDurationDays(Integer submittedDurationDays) {
        this.submittedDurationDays = submittedDurationDays;
    }

    public List<CropDiseaseKnowledgeDto> getPossibleCauses() {
        return possibleCauses;
    }

    public void setPossibleCauses(List<CropDiseaseKnowledgeDto> possibleCauses) {
        this.possibleCauses = possibleCauses;
    }

    public Boolean getIsUrgentKvkConsultationRecommended() {
        return isUrgentKvkConsultationRecommended;
    }

    public void setIsUrgentKvkConsultationRecommended(Boolean isUrgentKvkConsultationRecommended) {
        this.isUrgentKvkConsultationRecommended = isUrgentKvkConsultationRecommended;
    }

    public String getKvkEscalationReason() {
        return kvkEscalationReason;
    }

    public void setKvkEscalationReason(String kvkEscalationReason) {
        this.kvkEscalationReason = kvkEscalationReason;
    }

    public String getDiagnosticDisclaimer() {
        return diagnosticDisclaimer;
    }

    public void setDiagnosticDisclaimer(String diagnosticDisclaimer) {
        this.diagnosticDisclaimer = diagnosticDisclaimer;
    }

    public String getChemicalAdvisoryNotice() {
        return chemicalAdvisoryNotice;
    }

    public void setChemicalAdvisoryNotice(String chemicalAdvisoryNotice) {
        this.chemicalAdvisoryNotice = chemicalAdvisoryNotice;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }
}
