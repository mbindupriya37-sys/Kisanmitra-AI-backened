package com.kisanmitra.service;

import com.kisanmitra.dto.CropRecommendationResultListDto;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.CropRecommendationRequestRepository;
import com.kisanmitra.repository.CropRequirementRepository;
import com.kisanmitra.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CropRecommendationServiceTest {

    @Mock
    private CropRecommendationRequestRepository cropRecommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CropRequirementRepository cropRequirementRepository;

    private CropRecommendationService cropRecommendationService;

    private User farmerA;
    private User farmerB;
    private User expertUser;
    private CropRecommendationRequest requestA;

    @BeforeEach
    void setUp() {
        CropRecommendationEngine realEngine = new CropRecommendationEngine();
        cropRecommendationService = new CropRecommendationService(
                cropRecommendationRequestRepository,
                userRepository,
                cropRequirementRepository,
                realEngine
        );

        farmerA = new User();
        farmerA.setId(10L);
        farmerA.setEmail("farmerA@test.com");
        farmerA.setFullName("Farmer A");
        farmerA.setRole(Role.FARMER);

        farmerB = new User();
        farmerB.setId(20L);
        farmerB.setEmail("farmerB@test.com");
        farmerB.setFullName("Farmer B");
        farmerB.setRole(Role.FARMER);

        expertUser = new User();
        expertUser.setId(30L);
        expertUser.setEmail("expert@test.com");
        expertUser.setFullName("Expert User");
        expertUser.setRole(Role.EXPERT);

        requestA = new CropRecommendationRequest();
        requestA.setId(100L);
        requestA.setFarmer(farmerA);
        requestA.setStatus(RecommendationStatus.PENDING);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(User user) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("16. Successful analysis changes request status to COMPLETED")
    void testSuccessfulAnalysisChangesStatusToCompleted() {
        authenticateAs(farmerA);

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(requestA));
        when(cropRequirementRepository.findAllActiveWithCrop()).thenReturn(List.of());
        when(cropRecommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CropRecommendationResultListDto result = cropRecommendationService.getRecommendationResults(100L);

        assertNotNull(result);
        assertEquals(RecommendationStatus.COMPLETED, result.getStatus());
        assertEquals(RecommendationStatus.COMPLETED, requestA.getStatus());
        verify(cropRecommendationRequestRepository).save(requestA);
    }

    @Test
    @DisplayName("17. Failed analysis changes request status to FAILED")
    void testFailedAnalysisChangesStatusToFailed() {
        authenticateAs(farmerA);

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(requestA));
        when(cropRequirementRepository.findAllActiveWithCrop()).thenThrow(new RuntimeException("Database error during evaluation"));
        when(cropRecommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            cropRecommendationService.getRecommendationResults(100L);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertEquals(RecommendationStatus.FAILED, requestA.getStatus());
        verify(cropRecommendationRequestRepository).save(requestA);
    }

    @Test
    @DisplayName("18. Farmer can retrieve their own recommendation results")
    void testFarmerCanRetrieveOwnResults() {
        authenticateAs(farmerA);

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(requestA));
        when(cropRequirementRepository.findAllActiveWithCrop()).thenReturn(List.of());
        when(cropRecommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CropRecommendationResultListDto result = cropRecommendationService.getRecommendationResults(100L);

        assertNotNull(result);
        assertEquals(100L, result.getRequestId());
    }

    @Test
    @DisplayName("19. Another farmer cannot retrieve the result (403 FORBIDDEN)")
    void testAnotherFarmerDeniedAccess() {
        authenticateAs(farmerB); // Farmer B trying to access Farmer A's request

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(requestA));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            cropRecommendationService.getRecommendationResults(100L);
        });

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Access denied"));
    }

    @Test
    @DisplayName("20. EXPERT role cannot access farmer recommendation results (403 FORBIDDEN)")
    void testExpertRoleDeniedAccess() {
        authenticateAs(expertUser);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            cropRecommendationService.getRecommendationResults(100L);
        });

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("21. Preserves state, district, village, PIN code and farm specifics in recommendation results")
    void testPreservesLocationAndFarmDetailsInResult() {
        authenticateAs(farmerA);

        requestA.setPincode("522001");
        requestA.setState("Andhra Pradesh");
        requestA.setDistrict("Guntur");
        requestA.setVillage("Tenali");
        requestA.setSoilType(SoilType.BLACK);
        requestA.setSeason(Season.KHARIF);
        requestA.setFarmSize(3.5);

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(requestA));
        when(cropRequirementRepository.findAllActiveWithCrop()).thenReturn(List.of());
        when(cropRecommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CropRecommendationResultListDto result = cropRecommendationService.getRecommendationResults(100L);

        assertNotNull(result);
        assertEquals("522001", result.getPincode());
        assertEquals("Andhra Pradesh", result.getState());
        assertEquals("Guntur", result.getDistrict());
        assertEquals("Tenali", result.getVillage());
        assertEquals("Tenali", result.getLocality());
        assertEquals(SoilType.BLACK, result.getSoilType());
        assertEquals(Season.KHARIF, result.getSeason());
        assertEquals(3.5, result.getFarmSize());
        assertEquals(RecommendationStatus.COMPLETED, result.getStatus());
    }

    @Test
    @DisplayName("22. Empty crop requirement database returns empty recommendations without fabricating results")
    void testEmptyCropRequirementsReturnsEmptyRecommendationsWithDisclaimer() {
        authenticateAs(farmerA);

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(requestA));
        when(cropRequirementRepository.findAllActiveWithCrop()).thenReturn(List.of());
        when(cropRecommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CropRecommendationResultListDto result = cropRecommendationService.getRecommendationResults(100L);

        assertNotNull(result);
        assertTrue(result.getRecommendations().isEmpty());
        assertNotNull(result.getDisclaimer());
        assertTrue(result.getDisclaimer().contains("Consider local expert advice"));
    }

    @Test
    @DisplayName("23. Successful analysis with active requirements computes scores and preserves source attribution")
    void testSuccessfulAnalysisWithActiveCropRequirementEvaluatesScoreAndSource() {
        authenticateAs(farmerA);

        requestA.setSoilType(SoilType.BLACK);
        requestA.setSoilPh(6.5);
        requestA.setWaterAvailability(WaterAvailability.HIGH);
        requestA.setIrrigation(IrrigationType.YES);
        requestA.setExpectedRainfall(RainfallLevel.HIGH);
        requestA.setSeason(Season.KHARIF);
        requestA.setTemperatureRange(TemperatureRange.WARM);

        Crop crop = new Crop("Rice", "Oryza sativa", "Staple cereal crop");
        crop.setId(1L);
        CropRequirement req = new CropRequirement(crop);
        req.setId(10L);
        req.setMinSoilPh(5.5);
        req.setMaxSoilPh(7.5);
        req.setSuitableSoilTypes(java.util.Set.of(SoilType.BLACK, SoilType.ALLUVIAL));
        req.setSuitableSeasons(java.util.Set.of(Season.KHARIF));
        req.setMinRainfall(RainfallLevel.HIGH);
        req.setMaxRainfall(RainfallLevel.VERY_HIGH);
        req.setMinTemperatureRange(TemperatureRange.WARM);
        req.setMaxTemperatureRange(TemperatureRange.HOT);
        req.setMinWaterAvailability(WaterAvailability.HIGH);
        req.setMaxWaterAvailability(WaterAvailability.VERY_HIGH);
        req.setIrrigationRequired(true);
        req.setRiskLevel(CropRiskLevel.MEDIUM);
        req.setSourceName("ICAR-IIRR");
        req.setSourceUrl("https://www.icar-iirr.org");
        req.setSourceUpdatedDate(java.time.LocalDate.of(2024, 1, 15));

        when(cropRecommendationRequestRepository.findById(100L)).thenReturn(Optional.of(requestA));
        when(cropRequirementRepository.findAllActiveWithCrop()).thenReturn(List.of(req));
        when(cropRecommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CropRecommendationResultListDto result = cropRecommendationService.getRecommendationResults(100L);

        assertNotNull(result);
        assertEquals(1, result.getRecommendations().size());
        var rec = result.getRecommendations().get(0);
        assertEquals("Rice", rec.getCrop().getName());
        assertTrue(rec.getOverallScore() > 0.0);
        assertNotNull(rec.getSuitability());
        assertNotNull(rec.getSource());
        assertEquals("ICAR-IIRR", rec.getSource().getName());
        assertEquals("https://www.icar-iirr.org", rec.getSource().getUrl());
    }

    @Test
    @DisplayName("24. Request not found throws 404 NOT_FOUND")
    void testRequestNotFoundThrows404() {
        authenticateAs(farmerA);

        when(cropRecommendationRequestRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            cropRecommendationService.getRecommendationResults(999L);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
