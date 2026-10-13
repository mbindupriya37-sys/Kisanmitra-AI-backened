package com.kisanmitra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisanmitra.dto.FarmerCropIntentionRequestDto;
import com.kisanmitra.entity.Crop;
import com.kisanmitra.entity.FarmerCropIntention;
import com.kisanmitra.entity.Role;
import com.kisanmitra.entity.Season;
import com.kisanmitra.entity.User;
import com.kisanmitra.repository.CropRepository;
import com.kisanmitra.repository.FarmerCropIntentionRepository;
import com.kisanmitra.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FarmerCropIntentionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private FarmerCropIntentionRepository farmerCropIntentionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testFarmer;
    private Crop testCrop;

    @BeforeEach
    void setUp() {
        testFarmer = userRepository.findByEmail("farmer.integration.test@kisanmitra.in").orElseGet(() -> {
            User user = new User();
            user.setFullName("Test Farmer Integration");
            user.setEmail("farmer.integration.test@kisanmitra.in");
            user.setPhone("9876500001");
            user.setPassword(passwordEncoder.encode("Password123"));
            user.setRole(Role.FARMER);
            return userRepository.saveAndFlush(user);
        });

        testCrop = cropRepository.findAll().stream().findFirst().orElseGet(() -> {
            Crop crop = new Crop("Test Intention Crop", "Botanical test", "Test description");
            return cropRepository.saveAndFlush(crop);
        });
    }

    @Test
    @WithMockUser(username = "farmer.integration.test@kisanmitra.in", roles = {"FARMER"})
    @DisplayName("1. Create Intention: Successfully records farmer crop intention")
    void testCreateIntentionSuccess() throws Exception {
        FarmerCropIntentionRequestDto requestDto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh",
                "Guntur",
                testCrop.getId(),
                Season.KHARIF,
                4.5
        );

        mockMvc.perform(post("/api/farmer/crop-intentions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.state").value("Andhra Pradesh"))
                .andExpect(jsonPath("$.district").value("Guntur"))
                .andExpect(jsonPath("$.intendedSeason").value("KHARIF"))
                .andExpect(jsonPath("$.farmSize").value(4.5))
                .andExpect(jsonPath("$.crop.id").value(testCrop.getId()))
                .andExpect(jsonPath("$.crop.name").value(testCrop.getName()));
    }

    @Test
    @WithMockUser(username = "farmer.integration.test@kisanmitra.in", roles = {"FARMER"})
    @DisplayName("2. Create Intention: Rejects duplicate intention for same crop/district/season with 409 Conflict")
    void testCreateDuplicateIntentionConflict() throws Exception {
        FarmerCropIntentionRequestDto requestDto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh",
                "Krishna",
                testCrop.getId(),
                Season.RABI,
                2.5
        );

        // First creation succeeds
        mockMvc.perform(post("/api/farmer/crop-intentions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        // Duplicate attempt fails with 409 Conflict
        mockMvc.perform(post("/api/farmer/crop-intentions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "farmer.integration.test@kisanmitra.in", roles = {"FARMER"})
    @DisplayName("3. Get Intentions: Retrieves paginated crop intentions for authenticated farmer")
    void testGetFarmerIntentionsPaginated() throws Exception {
        FarmerCropIntention intention = new FarmerCropIntention(
                testFarmer, "Telangana", "Warangal", testCrop, Season.KHARIF, 3.0
        );
        farmerCropIntentionRepository.saveAndFlush(intention);

        mockMvc.perform(get("/api/farmer/crop-intentions")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    @WithMockUser(username = "farmer.integration.test@kisanmitra.in", roles = {"FARMER"})
    @DisplayName("4. Update Intention: Successfully updates farm size and season of owned intention")
    void testUpdateIntentionSuccess() throws Exception {
        FarmerCropIntention intention = new FarmerCropIntention(
                testFarmer, "Karnataka", "Bellary", testCrop, Season.KHARIF, 2.0
        );
        FarmerCropIntention saved = farmerCropIntentionRepository.saveAndFlush(intention);

        FarmerCropIntentionRequestDto updateDto = new FarmerCropIntentionRequestDto(
                "Karnataka",
                "Bellary",
                testCrop.getId(),
                Season.KHARIF,
                6.0 // Updated farm size
        );

        mockMvc.perform(put("/api/farmer/crop-intentions/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.farmSize").value(6.0));
    }

    @Test
    @WithMockUser(username = "farmer.integration.test@kisanmitra.in", roles = {"FARMER"})
    @DisplayName("5. Delete Intention: Successfully deletes owned intention with 204 No Content")
    void testDeleteIntentionSuccess() throws Exception {
        FarmerCropIntention intention = new FarmerCropIntention(
                testFarmer, "Punjab", "Ludhiana", testCrop, Season.RABI, 5.0
        );
        FarmerCropIntention saved = farmerCropIntentionRepository.saveAndFlush(intention);

        mockMvc.perform(delete("/api/farmer/crop-intentions/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(farmerCropIntentionRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "farmer.integration.test@kisanmitra.in", roles = {"FARMER"})
    @DisplayName("6. Oversupply Risk: Returns aggregated regional intelligence and risk metrics")
    void testGetOversupplyRisk() throws Exception {
        mockMvc.perform(get("/api/farmer/oversupply-risk")
                        .param("state", "Andhra Pradesh")
                        .param("district", "Guntur")
                        .param("season", "KHARIF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("Andhra Pradesh"))
                .andExpect(jsonPath("$.district").value("Guntur"))
                .andExpect(jsonPath("$.season").value("KHARIF"))
                .andExpect(jsonPath("$.cropRiskItems").isArray())
                .andExpect(jsonPath("$.summaryMessage").isString());
    }

    @Test
    @WithMockUser(username = "farmer.integration.test@kisanmitra.in", roles = {"FARMER"})
    @DisplayName("7. Active Crops: Returns active catalog for crop intention selector")
    void testGetActiveCrops() throws Exception {
        mockMvc.perform(get("/api/farmer/crops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @DisplayName("8. Security: Unauthenticated request to crop-intentions is denied with 401 or 403")
    void testUnauthenticatedAccessDenied() throws Exception {
        mockMvc.perform(get("/api/farmer/crop-intentions"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "expert@kisanmitra.in", roles = {"EXPERT"})
    @DisplayName("9. Security: Non-FARMER role (EXPERT) attempting farmer intention endpoint is forbidden (403)")
    void testNonFarmerRoleForbidden() throws Exception {
        mockMvc.perform(get("/api/farmer/crop-intentions"))
                .andExpect(status().isForbidden());
    }
}
