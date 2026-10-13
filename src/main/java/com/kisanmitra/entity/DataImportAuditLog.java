package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "data_import_audit_logs",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_import_audit_checksum",
            columnNames = {"file_checksum_sha256"}
        )
    }
)
public class DataImportAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "domain_dataset", nullable = false, length = 50)
    private String domainDataset;

    @Column(name = "source_file_name", nullable = false, length = 255)
    private String sourceFileName;

    @Column(name = "source_file_version", nullable = false, length = 50)
    private String sourceFileVersion;

    @Column(name = "file_checksum_sha256", nullable = false, length = 64)
    private String fileChecksumSha256;

    @Column(name = "total_records_parsed", nullable = false)
    private Integer totalRecordsParsed = 0;

    @Column(name = "inserted_records", nullable = false)
    private Integer insertedRecords = 0;

    @Column(name = "superseded_records", nullable = false)
    private Integer supersededRecords = 0;

    @Column(name = "rejected_records", nullable = false)
    private Integer rejectedRecords = 0;

    @Column(name = "execution_status", nullable = false, length = 50)
    private String executionStatus; // SUCCESS, FAILED, ALREADY_IMPORTED

    @Column(name = "error_summary", columnDefinition = "TEXT")
    private String errorSummary;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "auditLog", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DataImportRejectedRow> rejectedRows = new ArrayList<>();

    public DataImportAuditLog() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDomainDataset() { return domainDataset; }
    public void setDomainDataset(String domainDataset) { this.domainDataset = domainDataset; }

    public String getSourceFileName() { return sourceFileName; }
    public void setSourceFileName(String sourceFileName) { this.sourceFileName = sourceFileName; }

    public String getSourceFileVersion() { return sourceFileVersion; }
    public void setSourceFileVersion(String sourceFileVersion) { this.sourceFileVersion = sourceFileVersion; }

    public String getFileChecksumSha256() { return fileChecksumSha256; }
    public void setFileChecksumSha256(String fileChecksumSha256) { this.fileChecksumSha256 = fileChecksumSha256; }

    public Integer getTotalRecordsParsed() { return totalRecordsParsed; }
    public void setTotalRecordsParsed(Integer totalRecordsParsed) { this.totalRecordsParsed = totalRecordsParsed; }

    public Integer getInsertedRecords() { return insertedRecords; }
    public void setInsertedRecords(Integer insertedRecords) { this.insertedRecords = insertedRecords; }

    public Integer getSupersededRecords() { return supersededRecords; }
    public void setSupersededRecords(Integer supersededRecords) { this.supersededRecords = supersededRecords; }

    public Integer getRejectedRecords() { return rejectedRecords; }
    public void setRejectedRecords(Integer rejectedRecords) { this.rejectedRecords = rejectedRecords; }

    public String getExecutionStatus() { return executionStatus; }
    public void setExecutionStatus(String executionStatus) { this.executionStatus = executionStatus; }

    public String getErrorSummary() { return errorSummary; }
    public void setErrorSummary(String errorSummary) { this.errorSummary = errorSummary; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<DataImportRejectedRow> getRejectedRows() { return rejectedRows; }
    public void setRejectedRows(List<DataImportRejectedRow> rejectedRows) { this.rejectedRows = rejectedRows; }
}
