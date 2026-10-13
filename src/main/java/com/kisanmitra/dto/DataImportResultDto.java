package com.kisanmitra.dto;

import java.util.ArrayList;
import java.util.List;

public class DataImportResultDto {

    private String fileName;
    private String fileChecksumSha256;
    private String status; // SUCCESS, ALREADY_IMPORTED, REJECTED, FAILED
    private int totalParsed;
    private int insertedCount;
    private int supersededCount;
    private int rejectedCount;
    private String message;
    private List<String> validationErrors = new ArrayList<>();

    public DataImportResultDto() {
    }

    public DataImportResultDto(String fileName, String fileChecksumSha256, String status, String message) {
        this.fileName = fileName;
        this.fileChecksumSha256 = fileChecksumSha256;
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileChecksumSha256() { return fileChecksumSha256; }
    public void setFileChecksumSha256(String fileChecksumSha256) { this.fileChecksumSha256 = fileChecksumSha256; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalParsed() { return totalParsed; }
    public void setTotalParsed(int totalParsed) { this.totalParsed = totalParsed; }

    public int getInsertedCount() { return insertedCount; }
    public void setInsertedCount(int insertedCount) { this.insertedCount = insertedCount; }

    public int getSupersededCount() { return supersededCount; }
    public void setSupersededCount(int supersededCount) { this.supersededCount = supersededCount; }

    public int getRejectedCount() { return rejectedCount; }
    public void setRejectedCount(int rejectedCount) { this.rejectedCount = rejectedCount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(List<String> validationErrors) { this.validationErrors = validationErrors; }
}
