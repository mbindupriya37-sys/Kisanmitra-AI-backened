package com.kisanmitra.controller;

import com.kisanmitra.dto.AdminSystemSummaryDto;
import com.kisanmitra.entity.DataImportAuditLog;
import com.kisanmitra.repository.CropDiseaseKnowledgeRepository;
import com.kisanmitra.repository.CropRepository;
import com.kisanmitra.repository.DataImportAuditLogRepository;
import com.kisanmitra.repository.GovernmentSchemeRepository;
import com.kisanmitra.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final GovernmentSchemeRepository schemeRepository;
    private final CropRepository cropRepository;
    private final CropDiseaseKnowledgeRepository diseaseKnowledgeRepository;
    private final UserRepository userRepository;
    private final DataImportAuditLogRepository auditLogRepository;

    public AdminController(GovernmentSchemeRepository schemeRepository,
                           CropRepository cropRepository,
                           CropDiseaseKnowledgeRepository diseaseKnowledgeRepository,
                           UserRepository userRepository,
                           DataImportAuditLogRepository auditLogRepository) {
        this.schemeRepository = schemeRepository;
        this.cropRepository = cropRepository;
        this.diseaseKnowledgeRepository = diseaseKnowledgeRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> getAdminDashboard() {
        return ResponseEntity.ok(Collections.singletonMap("message", "Admin dashboard access granted"));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminSystemSummaryDto> getSystemSummary() {
        AdminSystemSummaryDto summary = new AdminSystemSummaryDto(
                schemeRepository.count(),
                cropRepository.count(),
                diseaseKnowledgeRepository.count(),
                userRepository.count(),
                auditLogRepository.count(),
                "OPERATIONAL",
                "ISOLATED_VERIFIED",
                LocalDateTime.now()
        );
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DataImportAuditLog>> getAuditLogs() {
        List<DataImportAuditLog> logs = auditLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }
}

