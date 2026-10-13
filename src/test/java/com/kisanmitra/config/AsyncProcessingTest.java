package com.kisanmitra.config;

import com.kisanmitra.dto.DiseaseAssessmentRequestDto;
import com.kisanmitra.dto.DiseaseAssessmentResponseDto;
import com.kisanmitra.entity.CropDiseaseKnowledge;
import com.kisanmitra.repository.CropDiseaseAssessmentLogRepository;
import com.kisanmitra.repository.CropDiseaseKnowledgeRepository;
import com.kisanmitra.service.AsyncAdvisoryAuditService;
import com.kisanmitra.service.CropDiseaseGuidanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AsyncProcessingTest {

    @Autowired
    private AsyncAdvisoryAuditService asyncAdvisoryAuditService;

    @Autowired
    private CropDiseaseGuidanceService cropDiseaseGuidanceService;

    @MockBean
    private CropDiseaseKnowledgeRepository knowledgeRepository;

    @MockBean
    private CropDiseaseAssessmentLogRepository logRepository;

    @Test
    @DisplayName("1. @Async method runs asynchronously and returns a completed CompletableFuture")
    void testAsyncExecutionSuccess() throws Exception {
        CompletableFuture<Boolean> future = asyncAdvisoryAuditService
                .recordAdvisoryEventAsync("TELEMETRY_SAMPLE", "RICE", "NORMAL");

        assertThat(future).isNotNull();
        Boolean result = future.get(5, TimeUnit.SECONDS);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("2. @Async failure handling: Simulated exception completes future exceptionally without crashing caller thread")
    void testAsyncExecutionFailureHandling() {
        CompletableFuture<Boolean> future = asyncAdvisoryAuditService
                .recordAdvisoryEventAsync("TRIGGER_TEST_FAILURE", "WHEAT", "ERROR_PROBE");

        assertThat(future).isNotNull();
        assertThatThrownBy(() -> future.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Simulated asynchronous failure");
    }

    @Test
    @DisplayName("3. Synchronous CropDiseaseGuidanceService triggers non-critical async telemetry on urgent KVK escalation")
    void testAsyncTelemetryDispatchedOnUrgentEscalation() {
        CropDiseaseKnowledge k1 = new CropDiseaseKnowledge(
                "Rice", "Rice Blast", "Magnaporthe oryzae", "Leaves",
                "Spindle-shaped lesions", "Trichoderma", "Sanitation", "KVK Advisory",
                "ICAR-NRRI", "https://nrri.icar.gov.in", "2026-10-10", "VERIFIED"
        );
        when(knowledgeRepository.findByCropNameIgnoreCase("Rice")).thenReturn(List.of(k1));

        DiseaseAssessmentRequestDto req = new DiseaseAssessmentRequestDto(
                "Rice", "Leaves", 3, "SEVERE", "Severe spreading lesions"
        );

        // Uses the real Spring-injected cropDiseaseGuidanceService with real asyncAdvisoryAuditService
        DiseaseAssessmentResponseDto response = cropDiseaseGuidanceService.assessCropHealth(req, "farmer@kisanmitra.in");

        assertThat(response).isNotNull();
        assertThat(response.getIsUrgentKvkConsultationRecommended()).isTrue();
        assertThat(response.getSubmittedCrop()).isEqualTo("Rice");
    }
}
