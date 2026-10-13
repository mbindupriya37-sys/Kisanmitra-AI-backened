package com.kisanmitra.service;

import com.kisanmitra.dto.CropDiseaseKnowledgeDto;
import com.kisanmitra.dto.DiseaseAssessmentRequestDto;
import com.kisanmitra.dto.DiseaseAssessmentResponseDto;
import com.kisanmitra.entity.CropDiseaseKnowledge;
import com.kisanmitra.exception.ResourceNotFoundException;
import com.kisanmitra.repository.CropDiseaseAssessmentLogRepository;
import com.kisanmitra.repository.CropDiseaseKnowledgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CropDiseaseGuidanceServiceTest {

    @Mock
    private CropDiseaseKnowledgeRepository knowledgeRepository;

    @Mock
    private CropDiseaseAssessmentLogRepository logRepository;

    private CropDiseaseGuidanceService guidanceService;

    @BeforeEach
    void setUp() {
        guidanceService = new CropDiseaseGuidanceService(knowledgeRepository, logRepository);
    }

    @Test
    @DisplayName("searchKnowledge should return DTO list matching crop and search query")
    void searchKnowledge_Success() {
        CropDiseaseKnowledge k1 = new CropDiseaseKnowledge(
                "Rice", "Rice Blast", "Magnaporthe oryzae", "Leaves",
                "Spindle-shaped lesions", "Trichoderma", "Resistant varieties", "KVK Advisory",
                "ICAR-NRRI", "https://nrri.icar.gov.in", "2026-10-08", "VERIFIED"
        );
        k1.setId(1L);

        when(knowledgeRepository.searchKnowledge("Rice", "Blast")).thenReturn(List.of(k1));

        List<CropDiseaseKnowledgeDto> results = guidanceService.searchKnowledge("Rice", "Blast");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Rice Blast", results.get(0).getDiseaseName());
        assertEquals("Magnaporthe oryzae", results.get(0).getScientificName());
    }

    @Test
    @DisplayName("getKnowledgeById should return entry when found")
    void getKnowledgeById_Success() {
        CropDiseaseKnowledge k1 = new CropDiseaseKnowledge(
                "Wheat", "Yellow Rust", "Puccinia striiformis", "Leaves",
                "Yellow pustules in linear stripes", "Resistant varieties", "Eradicate barberry", "KVK Advisory",
                "ICAR-IIWBR", "https://iiwbr.icar.gov.in", "2026-10-08", "VERIFIED"
        );
        k1.setId(2L);

        when(knowledgeRepository.findById(2L)).thenReturn(Optional.of(k1));

        CropDiseaseKnowledgeDto dto = guidanceService.getKnowledgeById(2L);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("Yellow Rust", dto.getDiseaseName());
    }

    @Test
    @DisplayName("getKnowledgeById should throw ResourceNotFoundException when not found")
    void getKnowledgeById_NotFound() {
        when(knowledgeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> guidanceService.getKnowledgeById(99L));
    }

    @Test
    @DisplayName("assessCropHealth should match candidate diseases and escalate severe symptoms to KVK")
    void assessCropHealth_SevereEscalation() {
        CropDiseaseKnowledge k1 = new CropDiseaseKnowledge(
                "Rice", "Rice Blast", "Magnaporthe oryzae", "Leaves",
                "Spindle-shaped lesions with grey center", "Trichoderma", "Sanitation", "KVK Advisory",
                "ICAR-NRRI", "https://nrri.icar.gov.in", "2026-10-08", "VERIFIED"
        );

        when(knowledgeRepository.findByCropNameIgnoreCase("Rice")).thenReturn(List.of(k1));

        DiseaseAssessmentRequestDto req = new DiseaseAssessmentRequestDto(
                "Rice", "Leaves", 3, "SEVERE", "Spindle-shaped lesions observed on leaves"
        );

        DiseaseAssessmentResponseDto response = guidanceService.assessCropHealth(req, "farmer@kisanmitra.ai");

        assertNotNull(response);
        assertEquals("Rice", response.getSubmittedCrop());
        assertEquals(1, response.getPossibleCauses().size());
        assertEquals("Rice Blast", response.getPossibleCauses().get(0).getDiseaseName());
        assertTrue(response.getIsUrgentKvkConsultationRecommended(), "Severe symptoms must escalate to KVK consultation");
        assertTrue(response.getDiagnosticDisclaimer().contains("possibilities based on ICAR"));
        assertTrue(response.getChemicalAdvisoryNotice().contains("CIBRC"));
        verify(logRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("assessCropHealth should escalate to KVK when no diseases match symptoms")
    void assessCropHealth_UnmatchedSymptoms_EscalatesToKvk() {
        when(knowledgeRepository.findByCropNameIgnoreCase("Groundnut")).thenReturn(Collections.emptyList());

        DiseaseAssessmentRequestDto req = new DiseaseAssessmentRequestDto(
                "Groundnut", "Roots", 10, "MODERATE", "Unclear root discoloration"
        );

        DiseaseAssessmentResponseDto response = guidanceService.assessCropHealth(req, "farmer@kisanmitra.ai");

        assertNotNull(response);
        assertTrue(response.getPossibleCauses().isEmpty());
        assertTrue(response.getIsUrgentKvkConsultationRecommended(), "Unmatched symptoms must escalate to KVK for lab pathology testing");
    }

    @Test
    @DisplayName("assessCropHealth should filter out VERIFICATION_REQUIRED entries from verified possible causes")
    void assessCropHealth_FiltersUnverifiedKnowledge() {
        CropDiseaseKnowledge verifiedEntry = new CropDiseaseKnowledge(
                "Rice", "Rice Blast", "Magnaporthe oryzae", "Leaves",
                "Spindle-shaped lesions", "Trichoderma", "Sanitation", "KVK Advisory",
                "ICAR-NRRI", "https://nrri.icar.gov.in", "2026-10-09", "VERIFIED"
        );

        CropDiseaseKnowledge unverifiedEntry = new CropDiseaseKnowledge(
                "Rice", "Rice Grassy Stunt", "RGSV Virus", "Whole Plant",
                "Spindle-shaped lesions with severe stunting", "Unsubstantiated herbal spray", "Vector control", "KVK Advisory",
                "ICAR-IIRR Note", "https://icar-iirr.org/preliminary", "2026-10-09", "VERIFICATION_REQUIRED"
        );

        when(knowledgeRepository.findByCropNameIgnoreCase("Rice")).thenReturn(List.of(verifiedEntry, unverifiedEntry));

        DiseaseAssessmentRequestDto req = new DiseaseAssessmentRequestDto(
                "Rice", "Leaves", 3, "MODERATE", "Spindle-shaped lesions on leaves"
        );

        DiseaseAssessmentResponseDto response = guidanceService.assessCropHealth(req, "farmer@kisanmitra.ai");

        assertNotNull(response);
        assertEquals(1, response.getPossibleCauses().size(), "Only VERIFIED entries must be returned in verified possible causes list");
        assertEquals("Rice Blast", response.getPossibleCauses().get(0).getDiseaseName());
        assertTrue(response.getKvkEscalationReason().contains("potential match(es) require secondary ICAR literature verification"));
    }

    @Test
    @DisplayName("assessCropHealth should escalate when conflicting symptoms match multiple diseases across categories")
    void assessCropHealth_ConflictingSymptoms_EscalatesToKvk() {
        CropDiseaseKnowledge k1 = new CropDiseaseKnowledge("Rice", "Rice Blast", "Magnaporthe oryzae", "Leaves", "Lesions", "Mgmt", "Prev", "KVK", "ICAR", "http://x", "2026-10-09", "VERIFIED");
        CropDiseaseKnowledge k2 = new CropDiseaseKnowledge("Rice", "Rice BLB", "Xanthomonas oryzae", "Leaves", "Lesions", "Mgmt", "Prev", "KVK", "ICAR", "http://x", "2026-10-09", "VERIFIED");
        CropDiseaseKnowledge k3 = new CropDiseaseKnowledge("Rice", "Rice Sheath Rot", "Sarocladium oryzae", "Leaves", "Lesions", "Mgmt", "Prev", "KVK", "ICAR", "http://x", "2026-10-09", "VERIFIED");

        when(knowledgeRepository.findByCropNameIgnoreCase("Rice")).thenReturn(List.of(k1, k2, k3));

        DiseaseAssessmentRequestDto req = new DiseaseAssessmentRequestDto(
                "Rice", "Leaves", 5, "MODERATE", "Lesions on leaves with wet and dry spots"
        );

        DiseaseAssessmentResponseDto response = guidanceService.assessCropHealth(req, "farmer@kisanmitra.ai");

        assertNotNull(response);
        assertEquals(3, response.getPossibleCauses().size());
        assertTrue(response.getIsUrgentKvkConsultationRecommended());
        assertTrue(response.getKvkEscalationReason().contains("multiple conflicting symptom patterns were detected"));
    }

    @Test
    @DisplayName("getAssessmentHistoryForFarmer should return isolated logs for authenticated farmer email")
    void getAssessmentHistoryForFarmer_IsolatesUserLogs() {
        String farmerEmail = "farmer1@kisanmitra.ai";
        com.kisanmitra.entity.CropDiseaseAssessmentLog log1 = new com.kisanmitra.entity.CropDiseaseAssessmentLog(
                farmerEmail, "Rice", "Leaves", 3, "MODERATE", "Lesions", "Rice Blast", false
        );

        when(logRepository.findByFarmerEmailOrderByCreatedAtDesc(farmerEmail)).thenReturn(List.of(log1));

        List<com.kisanmitra.entity.CropDiseaseAssessmentLog> logs = guidanceService.getAssessmentHistoryForFarmer(farmerEmail);

        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(farmerEmail, logs.get(0).getFarmerEmail());
        verify(logRepository, times(1)).findByFarmerEmailOrderByCreatedAtDesc(farmerEmail);
    }
}
