package com.kisanmitra.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_import_rejected_rows")
public class DataImportRejectedRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_log_id", nullable = false)
    private DataImportAuditLog auditLog;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @Column(name = "raw_line_content", nullable = false, columnDefinition = "TEXT")
    private String rawLineContent;

    @Column(name = "rejection_reason", nullable = false, length = 255)
    private String rejectionReason;

    @Column(name = "rejected_field", length = 100)
    private String rejectedField;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DataImportRejectedRow() {
    }

    public DataImportRejectedRow(DataImportAuditLog auditLog, Integer lineNumber, String rawLineContent, String rejectionReason, String rejectedField) {
        this.auditLog = auditLog;
        this.lineNumber = lineNumber;
        this.rawLineContent = rawLineContent;
        this.rejectionReason = rejectionReason;
        this.rejectedField = rejectedField;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DataImportAuditLog getAuditLog() { return auditLog; }
    public void setAuditLog(DataImportAuditLog auditLog) { this.auditLog = auditLog; }

    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }

    public String getRawLineContent() { return rawLineContent; }
    public void setRawLineContent(String rawLineContent) { this.rawLineContent = rawLineContent; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getRejectedField() { return rejectedField; }
    public void setRejectedField(String rejectedField) { this.rejectedField = rejectedField; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
