package com.kisanmitra.service;

import com.kisanmitra.dto.CropDiseaseKnowledgeDto;
import com.kisanmitra.dto.DiseaseAssessmentRequestDto;
import com.kisanmitra.dto.DiseaseAssessmentResponseDto;
import com.kisanmitra.entity.CropDiseaseAssessmentLog;
import com.kisanmitra.entity.CropDiseaseKnowledge;
import com.kisanmitra.exception.ResourceNotFoundException;
import com.kisanmitra.repository.CropDiseaseAssessmentLogRepository;
import com.kisanmitra.repository.CropDiseaseKnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropDiseaseGuidanceService {

    private static final String DIAGNOSTIC_DISCLAIMER =
            "DISCLAIMER: This guidance provides evidence-backed possibilities based on ICAR & State Agricultural University literature. " +
            "It is NOT a confirmed laboratory diagnosis. Always verify symptoms with local KVK agricultural extension officers or plant pathologists before taking management action.";

    private static final String CHEMICAL_ADVISORY_NOTICE =
            "CHEMICAL SAFETY NOTICE: KisanMitra AI strictly prioritizes non-chemical management and cultural prevention. " +
            "No unverified pesticide doses or chemical mixtures are recommended. Any chemical application must adhere to Central Insecticides Board & Registration Committee (CIBRC) labels and local KVK guidance.";

    private final CropDiseaseKnowledgeRepository knowledgeRepository;
    private final CropDiseaseAssessmentLogRepository logRepository;
    private final AsyncAdvisoryAuditService asyncAdvisoryAuditService;

    public CropDiseaseGuidanceService(CropDiseaseKnowledgeRepository knowledgeRepository,
                                      CropDiseaseAssessmentLogRepository logRepository) {
        this(knowledgeRepository, logRepository, null);
    }

    @Autowired
    public CropDiseaseGuidanceService(CropDiseaseKnowledgeRepository knowledgeRepository,
                                      CropDiseaseAssessmentLogRepository logRepository,
                                      @Autowired(required = false) AsyncAdvisoryAuditService asyncAdvisoryAuditService) {
        this.knowledgeRepository = knowledgeRepository;
        this.logRepository = logRepository;
        this.asyncAdvisoryAuditService = asyncAdvisoryAuditService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cropDiseaseKnowledge", key = "{#cropName != null ? #cropName.trim().toLowerCase() : '', #keyword != null ? #keyword.trim().toLowerCase() : ''}")
    public List<CropDiseaseKnowledgeDto> searchKnowledge(String cropName, String keyword) {
        String effectiveCrop = (cropName != null && !cropName.trim().isEmpty()) ? cropName.trim() : null;
        String effectiveKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        return knowledgeRepository.searchKnowledge(effectiveCrop, effectiveKeyword)
                .stream()
                .map(CropDiseaseKnowledgeDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "cropDiseaseKnowledgeById", key = "#id")
    public CropDiseaseKnowledgeDto getKnowledgeById(Long id) {
        CropDiseaseKnowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop Disease Knowledge entry not found with id: " + id));
        return new CropDiseaseKnowledgeDto(knowledge);
    }

    @Transactional
    public DiseaseAssessmentResponseDto assessCropHealth(DiseaseAssessmentRequestDto request, String farmerEmail) {
        String crop = request.getCropName().trim();
        String part = request.getAffectedPart().trim();
        String severity = request.getSeverityLevel().trim().toUpperCase();
        Integer duration = request.getSymptomDurationDays() != null ? request.getSymptomDurationDays() : 1;
        String userSymptoms = request.getSymptomsDescription().trim().toLowerCase();

        List<CropDiseaseKnowledge> candidateDiseases = knowledgeRepository.findByCropNameIgnoreCase(crop);
        List<CropDiseaseKnowledge> matchedList = new ArrayList<>();
        int unverifiedCandidateCount = 0;

        for (CropDiseaseKnowledge disease : candidateDiseases) {
            boolean partMatches = disease.getAffectedPlantParts().toLowerCase().contains(part.toLowerCase())
                    || disease.getAffectedPlantParts().equalsIgnoreCase("Whole Plant")
                    || part.equalsIgnoreCase("Whole Plant");

            boolean symptomKeywordMatches = isSymptomKeywordMatch(userSymptoms, disease.getSymptomsDescription().toLowerCase());

            if (partMatches || symptomKeywordMatches) {
                if ("VERIFIED".equalsIgnoreCase(disease.getVerificationStatus())) {
                    matchedList.add(disease);
                } else {
                    unverifiedCandidateCount++;
                }
            }
        }

        // Evaluate Krishi Vigyan Kendra (KVK) Escalation Thresholds
        boolean hasConflictingSymptoms = matchedList.size() >= 3;
        boolean isUrgentEscalation = severity.equals("SEVERE")
                || duration > 14
                || matchedList.isEmpty()
                || userSymptoms.contains("wilt")
                || userSymptoms.contains("rotting")
                || userSymptoms.contains("spreading rapidly")
                || hasConflictingSymptoms
                || unverifiedCandidateCount > 0;

        StringBuilder escalationReason = new StringBuilder();
        if (isUrgentEscalation) {
            escalationReason.append("Urgent KVK / Extension Officer consultation is recommended because ");
            if (severity.equals("SEVERE")) {
                escalationReason.append("symptoms are reported as SEVERE; ");
            }
            if (duration > 14) {
                escalationReason.append("symptoms have persisted for >14 days; ");
            }
            if (matchedList.isEmpty()) {
                escalationReason.append("no verified disease pattern matched the reported symptoms (requires official plant pathology laboratory testing); ");
            }
            if (hasConflictingSymptoms) {
                escalationReason.append("multiple conflicting symptom patterns were detected across distinct disease categories; ");
            }
            if (userSymptoms.contains("wilt") || userSymptoms.contains("rotting")) {
                escalationReason.append("vascular wilting or root rot symptoms can cause rapid crop collapse; ");
            }
            if (unverifiedCandidateCount > 0) {
                escalationReason.append(unverifiedCandidateCount).append(" potential match(es) require secondary ICAR literature verification; ");
            }
            escalationReason.append("prompt expert field verification by local agricultural specialists will prevent yield loss.");
        } else {
            escalationReason.append("Routine monitoring advised. Consult local KVK or agricultural extension officers if symptoms intensify or spread.");
        }

        List<CropDiseaseKnowledgeDto> matchedDtos = matchedList.stream()
                .map(CropDiseaseKnowledgeDto::new)
                .collect(Collectors.toList());

        String matchedNamesStr = matchedList.stream()
                .map(CropDiseaseKnowledge::getDiseaseName)
                .collect(Collectors.joining(", "));

        // Log assessment for farmer history audit (strictly tied to authenticated identity)
        if (farmerEmail != null && !farmerEmail.trim().isEmpty()) {
            CropDiseaseAssessmentLog logEntry = new CropDiseaseAssessmentLog(
                    farmerEmail, crop, part, duration, severity, request.getSymptomsDescription(),
                    matchedNamesStr, isUrgentEscalation
            );
            logRepository.save(logEntry);
        }

        // Asynchronously dispatch non-critical escalation telemetry if urgent consultation required
        if (isUrgentEscalation && asyncAdvisoryAuditService != null) {
            try {
                asyncAdvisoryAuditService.recordAdvisoryEventAsync("KVK_URGENT_ESCALATION", crop, severity);
            } catch (Exception ignored) {
                // Non-critical background telemetry must never alter synchronous response
            }
        }

        return new DiseaseAssessmentResponseDto(
                crop, part, severity, duration,
                matchedDtos, isUrgentEscalation, escalationReason.toString(),
                DIAGNOSTIC_DISCLAIMER, CHEMICAL_ADVISORY_NOTICE, LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public List<CropDiseaseAssessmentLog> getAssessmentHistoryForFarmer(String farmerEmail) {
        if (farmerEmail == null || farmerEmail.trim().isEmpty()) {
            return List.of();
        }
        return logRepository.findByFarmerEmailOrderByCreatedAtDesc(farmerEmail.trim());
    }

    private boolean isSymptomKeywordMatch(String userDesc, String diseaseDesc) {
        String[] keywords = {"spot", "blast", "blight", "rust", "wilt", "rot", "yellow", "lesion", "pustule", "smut", "halo", "curl", "streak"};
        for (String kw : keywords) {
            if (userDesc.contains(kw) && diseaseDesc.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
