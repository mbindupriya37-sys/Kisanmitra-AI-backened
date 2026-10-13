package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.CropRecommendationRequestRepository;
import com.kisanmitra.repository.CropRequirementRepository;
import com.kisanmitra.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionLabServiceTest {

    @Mock
    private CropRecommendationRequestRepository cropRecommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CropRequirementRepository cropRequirementRepository;

    private DecisionLabService decisionLabService;

    private User farmerUser;
    private User otherUser;
    private CropRecommendationRequest originalRequest;
    private Crop maizeCrop;
    private Crop riceCrop;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        decisionLabService = new DecisionLabService(
                cropRecommendationRequestRepository,
                userRepository,
                cropRequirementRepository,
                new CropRecommendationEngine()
        );

        farmerUser = new User();
        farmerUser.setId(10L);
        farmerUser.setFullName("Farmer Test");
        farmerUser.setEmail("farmer@kisanmitra.com");
        farmerUser.setRole(Role.FARMER);

        otherUser = new User();
        otherUser.setId(20L);
        otherUser.setFullName("Other Farmer");
        otherUser.setEmail("other@kisanmitra.com");
        otherUser.setRole(Role.FARMER);

        maizeCrop = new Crop("Maize", "Zea mays", "Cereal crop");
        maizeCrop.setId(1L);

        riceCrop = new Crop("Rice", "Oryza sativa", "Paddy crop");
        riceCrop.setId(2L);

        originalRequest = new CropRecommendationRequest();
        originalRequest.setId(100L);
        originalRequest.setFarmer(farmerUser);
        originalRequest.setState("Andhra Pradesh");
        originalRequest.setDistrict("Guntur");
        originalRequest.setSoilType(SoilType.LOAMY);
        originalRequest.setSoilPh(6.5);
        originalRequest.setSoilPhUnknown(false);
        originalRequest.setWaterAvailability(WaterAvailability.MODERATE);
        originalRequest.setIrrigation(IrrigationType.YES);
        originalRequest.setExpectedRainfall(RainfallLevel.MODERATE);
        originalRequest.setSeason(Season.KHARIF);
        originalRequest.setTemperatureRange(TemperatureRange.WARM);
        originalRequest.setFarmSize(5.0);
        originalRequest.setStatus(RecommendationStatus.COMPLETED);
    }

    private void authenticate(User user) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(auth);
        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("simulate should succeed and reuse engine for authenticated farmer")
    void simulate_Success() {
        authenticate(farmerUser);

        DecisionLabRequestDto dto = new DecisionLabRequestDto(
                100L, 7.2, false, WaterAvailability.HIGH, RainfallLevel.HIGH,
                Season.KHARIF, TemperatureRange.WARM, IrrigationType.YES
        );

        CropRequirement req = new CropRequirement();
        req.setCrop(maizeCrop);
        req.setActive(true);
        req.setMinSoilPh(6.0);
        req.setMaxSoilPh(7.5);
        req.setSuitableSoilTypes(Set.of(SoilType.LOAMY));
        req.setSuitableSeasons(Set.of(Season.KHARIF));
        req.setMinWaterAvailability(WaterAvailability.LOW);
        req.setMaxWaterAvailability(WaterAvailability.HIGH);
        req.setMinRainfall(RainfallLevel.LOW);
        req.setMaxRainfall(RainfallLevel.HIGH);
        req.setMinTemperatureRange(TemperatureRange.COOL);
        req.setMaxTemperatureRange(TemperatureRange.HOT);
        req.setIrrigationRequired(false);
        req.setRiskLevel(CropRiskLevel.LOW);
        req.setSourceName("ICAR Guidelines");
        req.setSourceUrl("https://icar.org.in");

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(originalRequest));
        when(cropRequirementRepository.findByActiveTrue()).thenReturn(List.of(req));

        DecisionLabResponseDto response = decisionLabService.simulate(dto);

        assertNotNull(response);
        assertEquals(100L, response.getRequestId());
        assertNotNull(response.getOriginalResults());
        assertNotNull(response.getSimulatedResults());
        assertNotNull(response.getComparison());

        assertNotNull(response.getComparison().getOriginalTopCrop());
        assertNotNull(response.getComparison().getSimulatedTopCrop());
        assertEquals("Maize", response.getComparison().getOriginalTopCrop().getName());
        assertEquals("Maize", response.getComparison().getSimulatedTopCrop().getName());
        assertFalse(response.getComparison().getTopCropChanged());

        // Verify repository save was NEVER called (simulation is strictly temporary/in-memory)
        verify(cropRecommendationRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("simulate should fail if assessment belongs to another farmer")
    void simulate_CrossFarmerBlocked() {
        authenticate(otherUser);

        DecisionLabRequestDto dto = new DecisionLabRequestDto(
                100L, 7.2, false, WaterAvailability.HIGH, RainfallLevel.HIGH,
                Season.KHARIF, TemperatureRange.WARM, IrrigationType.YES
        );

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(originalRequest));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> decisionLabService.simulate(dto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("not owned by you"));
    }

    @Test
    @DisplayName("simulate should fail if soil pH is out of valid range")
    void simulate_InvalidSoilPh() {
        authenticate(farmerUser);

        DecisionLabRequestDto dto = new DecisionLabRequestDto(
                100L, 12.0, false, WaterAvailability.HIGH, RainfallLevel.HIGH,
                Season.KHARIF, TemperatureRange.WARM, IrrigationType.YES
        );

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(originalRequest));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> decisionLabService.simulate(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Soil pH must be between 4.0 and 9.5"));
    }
}
