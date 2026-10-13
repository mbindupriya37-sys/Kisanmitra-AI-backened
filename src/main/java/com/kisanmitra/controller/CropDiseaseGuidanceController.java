package com.kisanmitra.controller;

import com.kisanmitra.dto.CropDiseaseKnowledgeDto;
import com.kisanmitra.dto.DiseaseAssessmentRequestDto;
import com.kisanmitra.dto.DiseaseAssessmentResponseDto;
import com.kisanmitra.service.CropDiseaseGuidanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disease-guidance")
public class CropDiseaseGuidanceController {

    private final CropDiseaseGuidanceService guidanceService;

    public CropDiseaseGuidanceController(CropDiseaseGuidanceService guidanceService) {
        this.guidanceService = guidanceService;
    }

    @PostMapping("/assess")
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<DiseaseAssessmentResponseDto> assessCropHealth(
            @Valid @RequestBody DiseaseAssessmentRequestDto request,
            Authentication authentication) {
        String farmerEmail = (authentication != null) ? authentication.getName() : "anonymous_farmer";
        DiseaseAssessmentResponseDto response = guidanceService.assessCropHealth(request, farmerEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/knowledge")
    public ResponseEntity<List<CropDiseaseKnowledgeDto>> searchKnowledge(
            @RequestParam(required = false) String crop,
            @RequestParam(required = false) String search) {
        List<CropDiseaseKnowledgeDto> list = guidanceService.searchKnowledge(crop, search);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/knowledge/{id}")
    public ResponseEntity<CropDiseaseKnowledgeDto> getKnowledgeById(@PathVariable Long id) {
        CropDiseaseKnowledgeDto dto = guidanceService.getKnowledgeById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<List<com.kisanmitra.entity.CropDiseaseAssessmentLog>> getAssessmentHistory(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        List<com.kisanmitra.entity.CropDiseaseAssessmentLog> history = guidanceService.getAssessmentHistoryForFarmer(authentication.getName());
        return ResponseEntity.ok(history);
    }
}
