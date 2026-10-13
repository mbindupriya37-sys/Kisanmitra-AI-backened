package com.kisanmitra.dto;

import java.time.LocalDateTime;

public class AdminSystemSummaryDto {

    private long totalGovernmentSchemes;
    private long totalCrops;
    private long totalDiseaseRecords;
    private long totalUsers;
    private long totalAuditLogs;
    private String systemStatus;
    private String activeDatabaseProfile;
    private LocalDateTime serverTimestamp;

    public AdminSystemSummaryDto() {
    }

    public AdminSystemSummaryDto(long totalGovernmentSchemes,
                                 long totalCrops,
                                 long totalDiseaseRecords,
                                 long totalUsers,
                                 long totalAuditLogs,
                                 String systemStatus,
                                 String activeDatabaseProfile,
                                 LocalDateTime serverTimestamp) {
        this.totalGovernmentSchemes = totalGovernmentSchemes;
        this.totalCrops = totalCrops;
        this.totalDiseaseRecords = totalDiseaseRecords;
        this.totalUsers = totalUsers;
        this.totalAuditLogs = totalAuditLogs;
        this.systemStatus = systemStatus;
        this.activeDatabaseProfile = activeDatabaseProfile;
        this.serverTimestamp = serverTimestamp;
    }

    public long getTotalGovernmentSchemes() {
        return totalGovernmentSchemes;
    }

    public void setTotalGovernmentSchemes(long totalGovernmentSchemes) {
        this.totalGovernmentSchemes = totalGovernmentSchemes;
    }

    public long getTotalCrops() {
        return totalCrops;
    }

    public void setTotalCrops(long totalCrops) {
        this.totalCrops = totalCrops;
    }

    public long getTotalDiseaseRecords() {
        return totalDiseaseRecords;
    }

    public void setTotalDiseaseRecords(long totalDiseaseRecords) {
        this.totalDiseaseRecords = totalDiseaseRecords;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalAuditLogs() {
        return totalAuditLogs;
    }

    public void setTotalAuditLogs(long totalAuditLogs) {
        this.totalAuditLogs = totalAuditLogs;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }

    public String getActiveDatabaseProfile() {
        return activeDatabaseProfile;
    }

    public void setActiveDatabaseProfile(String activeDatabaseProfile) {
        this.activeDatabaseProfile = activeDatabaseProfile;
    }

    public LocalDateTime getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(LocalDateTime serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
