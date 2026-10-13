package com.kisanmitra.controller;

import com.kisanmitra.entity.CropDiseaseAssessmentLog;
import com.kisanmitra.repository.CropDiseaseAssessmentLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ExpertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CropDiseaseAssessmentLogRepository assessmentLogRepository;

    @Test
    @WithMockUser(username = "expert@kisanmitra.ai", roles = {"EXPERT"})
    @DisplayName("GET /api/expert/dashboard allows EXPERT role with 200 OK")
    void testGetExpertDashboardAllowsExpert() throws Exception {
        mockMvc.perform(get("/api/expert/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Expert dashboard access granted"));
    }

    @Test
    @WithMockUser(username = "expert@kisanmitra.ai", roles = {"EXPERT"})
    @DisplayName("GET /api/expert/escalations allows EXPERT role, returns escalated cases, and does NOT leak farmer email")
    void testGetExpertEscalationsAllowsExpertAndProtectsFarmerPrivacy() throws Exception {
        // Create an escalated log entry with sensitive farmer email
        CropDiseaseAssessmentLog escalatedLog = new CropDiseaseAssessmentLog(
                "confidential.farmer@kisanmitra.ai",
                "Tomato",
                "Leaves",
                5,
                "SEVERE",
                "Extensive black lesions on leaf margins with yellow halos",
                "Early Blight",
                true // KVK escalation recommended
        );
        assessmentLogRepository.saveAndFlush(escalatedLog);

        mockMvc.perform(get("/api/expert/escalations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cropName").value("Tomato"))
                .andExpect(jsonPath("$[0].severityLevel").value("SEVERE"))
                .andExpect(jsonPath("$[0].kvkConsultationRecommended").value(true))
                // CRITICAL PRIVACY ASSERTION: Farmer email must NEVER be present in the expert advisory response
                .andExpect(jsonPath("$[0].farmerEmail").doesNotExist())
                .andExpect(jsonPath("$[0].phone").doesNotExist());
    }

    @Test
    @WithMockUser(username = "farmer@kisanmitra.ai", roles = {"FARMER"})
    @DisplayName("GET /api/expert/escalations forbids FARMER role with 403 Forbidden")
    void testGetExpertEscalationsForbidsFarmer() throws Exception {
        mockMvc.perform(get("/api/expert/escalations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "farmer@kisanmitra.ai", roles = {"FARMER"})
    @DisplayName("GET /api/expert/dashboard forbids FARMER role with 403 Forbidden")
    void testGetExpertDashboardForbidsFarmer() throws Exception {
        mockMvc.perform(get("/api/expert/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/expert/escalations rejects unauthenticated request")
    void testGetExpertEscalationsRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/expert/escalations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
