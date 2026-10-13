package com.kisanmitra.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProvenanceMetaDto {

    private String sourceOrganization;
    private String sourceReferenceUrl;
    private LocalDate publicationDate;
    private String observationPeriod;
    private String geographicLevel;
    private String units;
    private LocalDateTime importTimestamp;
    private String dataStatus; // VERIFIED, PROVISIONAL, NOT_REPORTED, SUPERSEDED

    public ProvenanceMetaDto() {
    }

    public ProvenanceMetaDto(String sourceOrganization, String sourceReferenceUrl, LocalDate publicationDate,
                             String observationPeriod, String geographicLevel, String units,
                             LocalDateTime importTimestamp, String dataStatus) {
        this.sourceOrganization = sourceOrganization;
        this.sourceReferenceUrl = sourceReferenceUrl;
        this.publicationDate = publicationDate;
        this.observationPeriod = observationPeriod;
        this.geographicLevel = geographicLevel;
        this.units = units;
        this.importTimestamp = importTimestamp;
        this.dataStatus = dataStatus;
    }

    // Getters and Setters
    public String getSourceOrganization() { return sourceOrganization; }
    public void setSourceOrganization(String sourceOrganization) { this.sourceOrganization = sourceOrganization; }

    public String getSourceReferenceUrl() { return sourceReferenceUrl; }
    public void setSourceReferenceUrl(String sourceReferenceUrl) { this.sourceReferenceUrl = sourceReferenceUrl; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public String getObservationPeriod() { return observationPeriod; }
    public void setObservationPeriod(String observationPeriod) { this.observationPeriod = observationPeriod; }

    public String getGeographicLevel() { return geographicLevel; }
    public void setGeographicLevel(String geographicLevel) { this.geographicLevel = geographicLevel; }

    public String getUnits() { return units; }
    public void setUnits(String units) { this.units = units; }

    public LocalDateTime getImportTimestamp() { return importTimestamp; }
    public void setImportTimestamp(LocalDateTime importTimestamp) { this.importTimestamp = importTimestamp; }

    public String getDataStatus() { return dataStatus; }
    public void setDataStatus(String dataStatus) { this.dataStatus = dataStatus; }
}
