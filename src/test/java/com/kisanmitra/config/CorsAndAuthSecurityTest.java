package com.kisanmitra.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "CORS_ALLOWED_ORIGINS=https://kisanmitra-ai-chi.vercel.app,https://kisanmitra-fz9qrvmun-mbindupriya37-1052.vercel.app,http://localhost:5173,http://127.0.0.1:5173"
})
class CorsAndAuthSecurityTest {

    private static final String PREVIEW_ORIGIN =
            "https://kisanmitra-fz9qrvmun-mbindupriya37-1052.vercel.app";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void allowsPreviewOriginPreflightForRegistrationWithRequiredHeaders() throws Exception {
        MvcResult result = mockMvc.perform(options("/api/auth/register")
                        .header(HttpHeaders.ORIGIN, PREVIEW_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,authorization"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(PREVIEW_ORIGIN,
                result.getResponse().getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        String allowedMethods = result.getResponse().getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
        String allowedHeaders = result.getResponse().getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
        assertNotNull(allowedMethods);
        assertNotNull(allowedHeaders);
        assertTrue(allowedMethods.contains("POST"));
        assertTrue(allowedHeaders.toLowerCase().contains("authorization"));
        assertTrue(allowedHeaders.toLowerCase().contains("content-type"));
        assertFalse(result.getResponse().containsHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    void rejectsUnlistedOrigins() throws Exception {
        mockMvc.perform(options("/api/auth/register")
                        .header(HttpHeaders.ORIGIN, "https://untrusted.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden());
    }

    @Test
    void registrationAndLoginArePublicButStillValidateRequestBodies() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}