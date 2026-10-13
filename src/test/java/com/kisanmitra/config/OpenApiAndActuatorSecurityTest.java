package com.kisanmitra.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OpenApiAndActuatorSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OpenAPI openAPI;

    @Test
    @DisplayName("1. OpenApiConfig initializes comprehensive API metadata and JWT Bearer security scheme")
    void testOpenApiConfigMetadata() {
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("KisanMitra AI");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("Bearer Authentication");
        assertThat(openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication").getType().toString())
                .isEqualTo("http");
        assertThat(openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication").getScheme())
                .isEqualTo("bearer");
    }

    @Test
    @DisplayName("2. Swagger UI / OpenAPI v3 documentation endpoint is publicly accessible without auth")
    void testOpenApiDocsEndpointPublicAccess() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("KisanMitra AI — Full Stack Agricultural Advisory API"))
                .andExpect(jsonPath("$.paths").exists());
    }

    @Test
    @DisplayName("3. Swagger UI entry point returns OK or redirect without authentication")
    void testSwaggerUiEntryPoint() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("4. Actuator health endpoint is publicly accessible and returns UP status")
    void testActuatorHealthPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("5. Actuator metrics endpoint denies unauthenticated access (401 or 403)")
    void testActuatorMetricsUnauthenticatedDenied() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("6. Actuator metrics endpoint allows access to authenticated ADMIN user")
    @WithMockUser(username = "admin@kisanmitra.in", roles = {"ADMIN"})
    void testActuatorMetricsAdminAllowed() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray());
    }

    @Test
    @DisplayName("7. Sensitive Actuator endpoints (env, beans, shutdown) are not exposed and return 404")
    @WithMockUser(username = "admin@kisanmitra.in", roles = {"ADMIN"})
    void testSensitiveActuatorEndpointsNotExposed() throws Exception {
        // Environment / credentials must never be exposed
        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().isNotFound());

        // Internal Spring beans must not be exposed
        mockMvc.perform(get("/actuator/beans"))
                .andExpect(status().isNotFound());

        // Shutdown endpoint must not be exposed
        mockMvc.perform(get("/actuator/shutdown"))
                .andExpect(status().isNotFound());
    }
}
