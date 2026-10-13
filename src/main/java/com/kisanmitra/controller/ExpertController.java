package com.kisanmitra.controller;

import com.kisanmitra.dto.ExpertAdvisoryCaseDto;
import com.kisanmitra.entity.CropDiseaseAssessmentLog;
import com.kisanmitra.repository.CropDiseaseAssessmentLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expert")
public class ExpertController {

    private final CropDiseaseAssessmentLogRepository assessmentLogRepository;

    public ExpertController(CropDiseaseAssessmentLogRepository assessmentLogRepository) {
        this.assessmentLogRepository = assessmentLogRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<Map<String, String>> getExpertDashboard() {
        return ResponseEntity.ok(Collections.singletonMap("message", "Expert dashboard access granted"));
    }

    @GetMapping("/escalations")
    @PreAuthorize("hasRole('EXPERT')")
    public ResponseEntity<List<ExpertAdvisoryCaseDto>> getEscalatedCases() {
        List<CropDiseaseAssessmentLog> logs = assessmentLogRepository.findByKvkConsultationRecommendedTrueOrderByCreatedAtDesc();
        List<ExpertAdvisoryCaseDto> dtoList = logs.stream()
                .map(ExpertAdvisoryCaseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
}
