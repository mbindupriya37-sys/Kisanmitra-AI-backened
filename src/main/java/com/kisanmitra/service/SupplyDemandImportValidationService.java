package com.kisanmitra.service;

import com.kisanmitra.dto.DataImportResultDto;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SupplyDemandImportValidationService {

    private final DataImportAuditLogRepository auditLogRepository;
    private final DataImportRejectedRowRepository rejectedRowRepository;
    private final CropHistoricalProductionRepository productionRepository;
    private final CropRepository cropRepository;

    public SupplyDemandImportValidationService(
            DataImportAuditLogRepository auditLogRepository,
            DataImportRejectedRowRepository rejectedRowRepository,
            CropHistoricalProductionRepository productionRepository,
            CropRepository cropRepository) {
        this.auditLogRepository = auditLogRepository;
        this.rejectedRowRepository = rejectedRowRepository;
        this.productionRepository = productionRepository;
        this.cropRepository = cropRepository;
    }

    /**
     * Compute SHA-256 hash of CSV content.
     */
    public String calculateChecksum(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate SHA-256 checksum", e);
        }
    }

    /**
     * Atomic validation and staged ingestion for DES APY historical production observations.
     * Enforces all strict rules:
     * 1. Idempotency on SHA-256 checksum
     * 2. Missing data stored as NULL, never coerced to 0
     * 3. Native unit retention and audited conversion (Cotton 170kg, Jute 180kg)
     * 4. Atomic abort on validation failures
     */
    @Transactional
    public DataImportResultDto validateAndProcessDesApyCsv(String fileName, String csvContent, boolean publishEnabled) {
        String checksum = calculateChecksum(csvContent);

        // 1. Check idempotency
        Optional<DataImportAuditLog> existingLog = auditLogRepository.findByFileChecksumSha256(checksum);
        if (existingLog.isPresent() && "SUCCESS".equalsIgnoreCase(existingLog.get().getExecutionStatus())) {
            DataImportResultDto result = new DataImportResultDto(fileName, checksum, "ALREADY_IMPORTED",
                    "This file has already been ingested. Execution skipped idempotently with zero modifications.");
            result.setTotalParsed(existingLog.get().getTotalRecordsParsed());
            result.setInsertedCount(0);
            result.setSupersededCount(0);
            result.setRejectedCount(0);
            return result;
        }

        DataImportAuditLog auditLog = new DataImportAuditLog();
        auditLog.setDomainDataset("DES_APY_HISTORICAL_PRODUCTION");
        auditLog.setSourceFileName(fileName);
        auditLog.setSourceFileVersion("DES_APY_V1");
        auditLog.setFileChecksumSha256(checksum);
        auditLog.setStartedAt(LocalDateTime.now());
        auditLog.setExecutionStatus("STAGED_VALIDATING");

        List<DataImportRejectedRow> rejectedRows = new ArrayList<>();
        List<CropHistoricalProductionObservation> candidateRecords = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();

        int lineNum = 0;
        int parsedCount = 0;

        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            String headerLine = reader.readLine();
            lineNum++;
            if (headerLine == null) {
                return failAudit(auditLog, "File is completely empty", lineNum);
            }

            // Verify header schema
            String[] headers = headerLine.split(",");
            if (headers.length < 15 || !headerLine.toLowerCase().contains("source_crop_name") || !headerLine.toLowerCase().contains("production")) {
                return failAudit(auditLog, "Invalid CSV header schema for DES_APY_V1 format", lineNum);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                parsedCount++;
                String[] tokens = line.split(",", -1);
                if (tokens.length < 15) {
                    rejectedRows.add(new DataImportRejectedRow(auditLog, lineNum, line, "Insufficient columns in record", "columns"));
                    continue;
                }

                String sourceCrop = tokens[0].trim();
                String cropLookupName = tokens[1].trim();
                String sourceState = tokens[2].trim();
                String sourceDistrict = tokens[3].trim();
                String lgdCode = tokens[4].trim();
                String boundaryVintage = tokens[5].trim();
                String agYear = tokens[6].trim();
                String season = tokens[7].trim();
                String areaStr = tokens[8].trim();
                String prodStr = tokens[9].trim();
                String yieldStr = tokens[10].trim();
                String sourceRevision = tokens[11].trim();
                String sourceOrg = tokens[12].trim();
                String pubDateStr = tokens[13].trim();
                String sourceUrl = tokens[14].trim();

                // Resolve crop
                Optional<Crop> matchedCrop = cropRepository.findByNameIgnoreCase(cropLookupName);
                if (matchedCrop.isEmpty()) {
                    rejectedRows.add(new DataImportRejectedRow(auditLog, lineNum, line, "Unrecognized crop name: " + cropLookupName, "crop_code"));
                    continue;
                }

                // Parse area, production, yield without zero coercion
                BigDecimal area = parseDecimalOrNull(areaStr);
                BigDecimal rawProd = parseDecimalOrNull(prodStr);
                BigDecimal yield = parseDecimalOrNull(yieldStr);

                // Handle missing values
                String dataStatus = "VERIFIED";
                if (rawProd == null || isMissingIndicator(prodStr)) {
                    dataStatus = "NOT_REPORTED";
                }

                // Determine unit & conversion factor
                String rawUnit = "TONNES";
                BigDecimal conversionFactor = BigDecimal.ONE;
                String conversionRef = "DES_MoAFW_STANDARD_METRIC_TONNES";
                BigDecimal prodTonnes = rawProd;

                if (sourceCrop.toUpperCase().contains("COTTON")) {
                    rawUnit = "BALES_170KG";
                    conversionFactor = new BigDecimal("0.1700");
                    conversionRef = "DES_MoAFW_STATISTICAL_STANDARDS_170KG";
                    if (rawProd != null) {
                        prodTonnes = rawProd.multiply(conversionFactor).setScale(4, RoundingMode.HALF_UP);
                    }
                } else if (sourceCrop.toUpperCase().contains("JUTE") || sourceCrop.toUpperCase().contains("MESTA")) {
                    rawUnit = "BALES_180KG";
                    conversionFactor = new BigDecimal("0.1800");
                    conversionRef = "DES_MoAFW_STATISTICAL_STANDARDS_180KG";
                    if (rawProd != null) {
                        prodTonnes = rawProd.multiply(conversionFactor).setScale(4, RoundingMode.HALF_UP);
                    }
                }

                LocalDate pubDate = null;
                try {
                    if (!pubDateStr.isEmpty() && !isMissingIndicator(pubDateStr)) {
                        pubDate = LocalDate.parse(pubDateStr);
                    }
                } catch (Exception ex) {
                    rejectedRows.add(new DataImportRejectedRow(auditLog, lineNum, line, "Invalid publication date format", "publication_date"));
                    continue;
                }

                CropHistoricalProductionObservation obs = new CropHistoricalProductionObservation();
                obs.setCrop(matchedCrop.get());
                obs.setSourceCropName(sourceCrop);
                obs.setSourceState(sourceState);
                obs.setSourceDistrict(sourceDistrict);
                obs.setNormalizedState(sourceState.trim().toUpperCase());
                obs.setNormalizedDistrict(sourceDistrict.trim().toUpperCase());
                obs.setStateLgdCode(null);
                obs.setDistrictLgdCode(lgdCode.isEmpty() ? null : lgdCode);
                obs.setBoundaryVintage(boundaryVintage.isEmpty() ? "STANDARD_LGD_2024" : boundaryVintage);
                obs.setAgriculturalYear(agYear);
                obs.setSeason(season);
                obs.setAreaHectares(area);
                obs.setRawProductionValue(rawProd);
                obs.setRawUnit(rawUnit);
                obs.setProductionTonnes(prodTonnes);
                obs.setYieldKgPerHa(yield);
                obs.setConversionFactorApplied(conversionFactor);
                obs.setConversionVerificationRef(conversionRef);
                obs.setSourceRevision(sourceRevision);
                obs.setRevisionPriority(calculateRevisionPriority(sourceRevision));
                obs.setFileChecksumSha256(checksum);
                obs.setIsActive(true);
                obs.setDataStatus(dataStatus);
                obs.setSourceOrganization(sourceOrg.isEmpty() ? "DES_MoAFW" : sourceOrg);
                obs.setSourceReferenceUrl(sourceUrl);
                obs.setPublicationDate(pubDate);

                candidateRecords.add(obs);
            }
        } catch (Exception e) {
            return failAudit(auditLog, "I/O or parsing error: " + e.getMessage(), lineNum);
        }

        auditLog.setTotalRecordsParsed(parsedCount);
        auditLog.setRejectedRecords(rejectedRows.size());

        // Check if atomic rejection is required
        if (!rejectedRows.isEmpty()) {
            auditLog.setExecutionStatus("REJECTED_ERRORS_DETECTED");
            auditLog.setErrorSummary("Found " + rejectedRows.size() + " rejected rows during full-file validation.");
            auditLog.setCompletedAt(LocalDateTime.now());
            auditLogRepository.save(auditLog);

            for (DataImportRejectedRow rr : rejectedRows) {
                rr.setAuditLog(auditLog);
                rejectedRowRepository.save(rr);
                validationErrors.add("Line " + rr.getLineNumber() + " [" + rr.getRejectedField() + "]: " + rr.getRejectionReason());
            }

            DataImportResultDto errorResult = new DataImportResultDto(fileName, checksum, "REJECTED",
                    "Atomic validation failed with " + rejectedRows.size() + " errors. Zero records published.");
            errorResult.setTotalParsed(parsedCount);
            errorResult.setRejectedCount(rejectedRows.size());
            errorResult.setValidationErrors(validationErrors);
            return errorResult;
        }

        // Check if publishing is authorized
        if (!publishEnabled) {
            auditLog.setExecutionStatus("VALIDATED_PUBLISH_DISABLED");
            auditLog.setCompletedAt(LocalDateTime.now());
            auditLogRepository.save(auditLog);

            DataImportResultDto validResult = new DataImportResultDto(fileName, checksum, "VALIDATED_PUBLISH_DISABLED",
                    "File validated 100% successfully (" + candidateRecords.size() + " valid records). Publishing remains disabled per safety policy.");
            validResult.setTotalParsed(parsedCount);
            validResult.setInsertedCount(0);
            return validResult;
        }

        // Publish records atomically with revision supersession
        int inserted = 0;
        int superseded = 0;

        for (CropHistoricalProductionObservation cand : candidateRecords) {
            // Check existing active record
            Optional<CropHistoricalProductionObservation> existingActive = productionRepository.findActiveByCropAndSeason(
                    cand.getCrop().getId(), cand.getNormalizedState(), cand.getNormalizedDistrict(),
                    cand.getBoundaryVintage(), cand.getAgriculturalYear(), cand.getSeason());

            if (existingActive.isPresent()) {
                CropHistoricalProductionObservation old = existingActive.get();
                if (cand.getRevisionPriority() > old.getRevisionPriority()) {
                    old.setIsActive(false);
                    old.setDataStatus("SUPERSEDED");
                    old.setSupersededByBatchId(auditLog.getId());
                    productionRepository.save(old);
                    superseded++;
                } else {
                    // Lower priority - incoming record is saved as historical inactive
                    cand.setIsActive(false);
                    cand.setDataStatus("SUPERSEDED_BY_EXISTING");
                }
            }

            productionRepository.save(cand);
            inserted++;
        }

        auditLog.setInsertedRecords(inserted);
        auditLog.setSupersededRecords(superseded);
        auditLog.setExecutionStatus("SUCCESS");
        auditLog.setCompletedAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);

        DataImportResultDto successResult = new DataImportResultDto(fileName, checksum, "SUCCESS",
                "Successfully published " + inserted + " records (" + superseded + " older records superseded).");
        successResult.setTotalParsed(parsedCount);
        successResult.setInsertedCount(inserted);
        successResult.setSupersededCount(superseded);
        return successResult;
    }

    private boolean isMissingIndicator(String val) {
        if (val == null) return true;
        String trimmed = val.trim().toUpperCase();
        return trimmed.isEmpty() || trimmed.equals("NA") || trimmed.equals("NR") || trimmed.equals("@") || trimmed.equals("*") || trimmed.equals("NULL");
    }

    private BigDecimal parseDecimalOrNull(String val) {
        if (isMissingIndicator(val)) return null;
        try {
            return new BigDecimal(val.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private int calculateRevisionPriority(String revision) {
        if (revision == null) return 10;
        String upper = revision.toUpperCase();
        if (upper.contains("FINAL")) return 50;
        if (upper.contains("4TH") || upper.contains("FOURTH")) return 40;
        if (upper.contains("3RD") || upper.contains("THIRD")) return 30;
        if (upper.contains("2ND") || upper.contains("SECOND")) return 20;
        if (upper.contains("1ST") || upper.contains("FIRST")) return 10;
        return 5;
    }

    private DataImportResultDto failAudit(DataImportAuditLog auditLog, String reason, int line) {
        auditLog.setExecutionStatus("FAILED_SCHEMA");
        auditLog.setErrorSummary(reason);
        auditLog.setCompletedAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);

        DataImportResultDto result = new DataImportResultDto(auditLog.getSourceFileName(), auditLog.getFileChecksumSha256(), "FAILED", reason);
        result.getValidationErrors().add("Line " + line + ": " + reason);
        return result;
    }
}
