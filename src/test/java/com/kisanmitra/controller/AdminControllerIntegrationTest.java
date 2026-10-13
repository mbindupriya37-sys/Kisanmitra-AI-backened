package com.kisanmitra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisanmitra.dto.GovernmentSchemeRequestDto;
import com.kisanmitra.entity.SchemeCategory;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin@kisanmitra.ai", roles = {"ADMIN"})
    @DisplayName("GET /api/admin/dashboard allows ADMIN role with 200 OK")
    void testGetAdminDashboardAllowsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Admin dashboard access granted"));
    }

    @Test
    @WithMockUser(username = "admin@kisanmitra.ai", roles = {"ADMIN"})
    @DisplayName("GET /api/admin/summary allows ADMIN role and returns real system aggregate counts")
    void testGetAdminSummaryAllowsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.systemStatus").value("OPERATIONAL"))
                .andExpect(jsonPath("$.activeDatabaseProfile").value("ISOLATED_VERIFIED"))
                .andExpect(jsonPath("$.serverTimestamp").exists())
                .andExpect(jsonPath("$.totalGovernmentSchemes").isNumber())
                .andExpect(jsonPath("$.totalCrops").isNumber())
                .andExpect(jsonPath("$.totalDiseaseRecords").isNumber())
                .andExpect(jsonPath("$.totalUsers").isNumber())
                .andExpect(jsonPath("$.totalAuditLogs").isNumber());
    }

    @Test
    @WithMockUser(username = "admin@kisanmitra.ai", roles = {"ADMIN"})
    @DisplayName("GET /api/admin/audit-logs allows ADMIN role and returns audit list")
    void testGetAdminAuditLogsAllowsAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "farmer@kisanmitra.ai", roles = {"FARMER"})
    @DisplayName("GET /api/admin/summary forbids FARMER role with 403 Forbidden")
    void testGetAdminSummaryForbidsFarmer() throws Exception {
        mockMvc.perform(get("/api/admin/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "farmer@kisanmitra.ai", roles = {"FARMER"})
    @DisplayName("GET /api/admin/audit-logs forbids FARMER role with 403 Forbidden")
    void testGetAdminAuditLogsForbidsFarmer() throws Exception {
        mockMvc.perform(get("/api/admin/audit-logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/summary rejects unauthenticated request")
    void testGetAdminSummaryRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "farmer@kisanmitra.ai", roles = {"FARMER"})
    @DisplayName("POST /api/schemes forbids FARMER role with 403 Forbidden")
    void testPostSchemeForbidsFarmer() throws Exception {
        GovernmentSchemeRequestDto request = createSampleSchemeRequest("Unauthorized Scheme");

        mockMvc.perform(post("/api/schemes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@kisanmitra.ai", roles = {"ADMIN"})
    @DisplayName("POST /api/schemes allows ADMIN role with 201 Created")
    void testPostSchemeAllowsAdmin() throws Exception {
        GovernmentSchemeRequestDto request = createSampleSchemeRequest("Test PM-Kisan Admin Add");

        mockMvc.perform(post("/api/schemes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test PM-Kisan Admin Add"));
    }

    private GovernmentSchemeRequestDto createSampleSchemeRequest(String name) {
        GovernmentSchemeRequestDto dto = new GovernmentSchemeRequestDto();
        dto.setName(name);
        dto.setCategory(SchemeCategory.FINANCIAL_SUPPORT);
        dto.setMinistry("Ministry of Agriculture and Farmers Welfare");
        dto.setPurpose("Direct income support to eligible farmer families");
        dto.setEligibleApplicants("Small and marginal landholder farmer families");
        dto.setBenefits("Rs. 6000 per year in three installments");
        dto.setRequiredDocuments("Aadhaar Card, Land records, Bank account details");
        dto.setApplicationProcess("Online via official PM-Kisan portal or CSC centres");
        dto.setOfficialPortalUrl("https://pmkisan.gov.in");
        dto.setSourceName("Department of Agriculture & Farmers Welfare");
        dto.setLastVerifiedDate("2026-03-01");
        dto.setVerificationStatus("VERIFIED");
        return dto;
    }
}
