package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.CropRepository;
import com.kisanmitra.repository.FarmerCropIntentionRepository;
import com.kisanmitra.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmerCropIntentionServiceTest {

    @Mock
    private FarmerCropIntentionRepository farmerCropIntentionRepository;

    @Mock
    private CropRepository cropRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FarmerCropIntentionService farmerCropIntentionService;

    private User farmerUser;
    private User otherUser;
    private User thirdUser;
    private Crop maizeCrop;
    private Crop riceCrop;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

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

        thirdUser = new User();
        thirdUser.setId(30L);
        thirdUser.setFullName("Third Farmer");
        thirdUser.setEmail("third@kisanmitra.com");
        thirdUser.setRole(Role.FARMER);

        maizeCrop = new Crop("Maize", "Zea mays", "Staple cereal crop");
        maizeCrop.setId(1L);

        riceCrop = new Crop("Rice", "Oryza sativa", "Major food grain");
        riceCrop.setId(2L);
    }

    private void authenticate(User user) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("createIntention should save and return DTO when no duplicate exists")
    void createIntention_Success() {
        authenticate(farmerUser);

        FarmerCropIntentionRequestDto dto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh", "Guntur", 1L, Season.KHARIF, 5.0
        );

        when(cropRepository.findById(1L)).thenReturn(Optional.of(maizeCrop));
        when(farmerCropIntentionRepository.existsDuplicateIntention(10L, 1L, "Andhra Pradesh", "Guntur", Season.KHARIF))
                .thenReturn(false);
        when(farmerCropIntentionRepository.save(any(FarmerCropIntention.class)))
                .thenAnswer(invocation -> {
                    FarmerCropIntention saved = invocation.getArgument(0);
                    saved.setId(100L);
                    return saved;
                });

        FarmerCropIntentionResponseDto response = farmerCropIntentionService.createIntention(dto);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Andhra Pradesh", response.getState());
        assertEquals("Guntur", response.getDistrict());
        assertEquals(Season.KHARIF, response.getIntendedSeason());
        assertEquals(5.0, response.getFarmSize());
        assertEquals("Maize", response.getCrop().getName());
        verify(farmerCropIntentionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createIntention should reject duplicate submission by the same farmer with 409 Conflict")
    void createIntention_DuplicateRejected() {
        authenticate(farmerUser);

        FarmerCropIntentionRequestDto dto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh", "Guntur", 1L, Season.KHARIF, 5.0
        );

        when(cropRepository.findById(1L)).thenReturn(Optional.of(maizeCrop));
        when(farmerCropIntentionRepository.existsDuplicateIntention(10L, 1L, "Andhra Pradesh", "Guntur", Season.KHARIF))
                .thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> farmerCropIntentionService.createIntention(dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().contains("You have already recorded an intention for Maize"));
        assertTrue(ex.getReason().contains("KHARIF"));
        verify(farmerCropIntentionRepository, never()).save(any());
    }

    @Test
    @DisplayName("createIntention should allow the same farmer to record different crops in the same season and region")
    void createIntention_SameFarmerDifferentCrop_Allowed() {
        authenticate(farmerUser);

        FarmerCropIntentionRequestDto dto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh", "Guntur", 2L, Season.KHARIF, 4.0
        );

        when(cropRepository.findById(2L)).thenReturn(Optional.of(riceCrop));
        when(farmerCropIntentionRepository.existsDuplicateIntention(10L, 2L, "Andhra Pradesh", "Guntur", Season.KHARIF))
                .thenReturn(false);
        when(farmerCropIntentionRepository.save(any(FarmerCropIntention.class)))
                .thenAnswer(invocation -> {
                    FarmerCropIntention saved = invocation.getArgument(0);
                    saved.setId(101L);
                    return saved;
                });

        FarmerCropIntentionResponseDto response = farmerCropIntentionService.createIntention(dto);

        assertNotNull(response);
        assertEquals("Rice", response.getCrop().getName());
        verify(farmerCropIntentionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createIntention should allow the same crop to be recorded in a different season")
    void createIntention_SameCropDifferentSeason_Allowed() {
        authenticate(farmerUser);

        FarmerCropIntentionRequestDto dto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh", "Guntur", 1L, Season.RABI, 3.5
        );

        when(cropRepository.findById(1L)).thenReturn(Optional.of(maizeCrop));
        when(farmerCropIntentionRepository.existsDuplicateIntention(10L, 1L, "Andhra Pradesh", "Guntur", Season.RABI))
                .thenReturn(false);
        when(farmerCropIntentionRepository.save(any(FarmerCropIntention.class)))
                .thenAnswer(invocation -> {
                    FarmerCropIntention saved = invocation.getArgument(0);
                    saved.setId(102L);
                    return saved;
                });

        FarmerCropIntentionResponseDto response = farmerCropIntentionService.createIntention(dto);

        assertNotNull(response);
        assertEquals(Season.RABI, response.getIntendedSeason());
        verify(farmerCropIntentionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createIntention should normalize state and district whitespace and case")
    void createIntention_WhitespaceNormalization() {
        authenticate(farmerUser);

        FarmerCropIntentionRequestDto dto = new FarmerCropIntentionRequestDto(
                "   Andhra Pradesh   ", "  Guntur  ", 1L, Season.KHARIF, 5.0
        );

        when(cropRepository.findById(1L)).thenReturn(Optional.of(maizeCrop));
        when(farmerCropIntentionRepository.existsDuplicateIntention(10L, 1L, "Andhra Pradesh", "Guntur", Season.KHARIF))
                .thenReturn(false);

        ArgumentCaptor<FarmerCropIntention> captor = ArgumentCaptor.forClass(FarmerCropIntention.class);
        when(farmerCropIntentionRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        farmerCropIntentionService.createIntention(dto);

        FarmerCropIntention saved = captor.getValue();
        assertEquals("Andhra Pradesh", saved.getState());
        assertEquals("Guntur", saved.getDistrict());
    }

    @Test
    @DisplayName("createIntention should throw 404 if crop is not found")
    void createIntention_CropNotFound() {
        authenticate(farmerUser);

        FarmerCropIntentionRequestDto dto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh", "Guntur", 999L, Season.KHARIF, 5.0
        );

        when(cropRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> farmerCropIntentionService.createIntention(dto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Crop not found"));
    }

    @Test
    @DisplayName("getFarmerIntentions should return paginated intentions for authenticated farmer")
    void getFarmerIntentions_Success() {
        authenticate(farmerUser);

        FarmerCropIntention intention = new FarmerCropIntention(farmerUser, "Andhra Pradesh", "Guntur", maizeCrop, Season.KHARIF, 5.0);
        intention.setId(100L);

        when(farmerCropIntentionRepository.findByFarmerId(eq(10L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(intention)));

        PageResponse<FarmerCropIntentionResponseDto> pageResponse = farmerCropIntentionService.getFarmerIntentions(0, 5);

        assertNotNull(pageResponse);
        assertEquals(1, pageResponse.getContent().size());
        assertEquals(100L, pageResponse.getContent().get(0).getId());
    }

    @Test
    @DisplayName("updateIntention should reject duplicate when updating to an existing crop/season combination")
    void updateIntention_DuplicateRejected() {
        authenticate(farmerUser);

        FarmerCropIntention intention = new FarmerCropIntention(farmerUser, "Andhra Pradesh", "Guntur", maizeCrop, Season.KHARIF, 5.0);
        intention.setId(200L);

        FarmerCropIntentionRequestDto dto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh", "Guntur", 2L, Season.KHARIF, 10.0
        );

        when(farmerCropIntentionRepository.findByIdAndFarmerId(200L, 10L)).thenReturn(Optional.of(intention));
        when(cropRepository.findById(2L)).thenReturn(Optional.of(riceCrop));
        when(farmerCropIntentionRepository.existsDuplicateIntentionExcludingId(10L, 2L, "Andhra Pradesh", "Guntur", Season.KHARIF, 200L))
                .thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> farmerCropIntentionService.updateIntention(200L, dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Another intention for Rice"));
        verify(farmerCropIntentionRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateIntention should fail if record is not owned by authenticated farmer")
    void updateIntention_CrossFarmerBlocked() {
        authenticate(farmerUser);

        FarmerCropIntentionRequestDto dto = new FarmerCropIntentionRequestDto(
                "Andhra Pradesh", "Guntur", 1L, Season.KHARIF, 10.0
        );

        when(farmerCropIntentionRepository.findByIdAndFarmerId(200L, 10L))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> farmerCropIntentionService.updateIntention(200L, dto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("not owned by you"));
    }

    @Test
    @DisplayName("deleteIntention should succeed if owned by authenticated farmer")
    void deleteIntention_Success() {
        authenticate(farmerUser);

        FarmerCropIntention intention = new FarmerCropIntention(farmerUser, "Andhra Pradesh", "Guntur", maizeCrop, Season.KHARIF, 5.0);
        intention.setId(100L);

        when(farmerCropIntentionRepository.findByIdAndFarmerId(100L, 10L))
                .thenReturn(Optional.of(intention));

        assertDoesNotThrow(() -> farmerCropIntentionService.deleteIntention(100L));
        verify(farmerCropIntentionRepository, times(1)).delete(intention);
    }

    @Test
    @DisplayName("Three records belonging to only one farmer must remain INSUFFICIENT_DATA")
    void getOversupplyRisk_ThreeRecordsFromSingleFarmer_InsufficientData() {
        authenticate(farmerUser);

        // 3 intentions, but ALL from farmerUser (id: 10L)
        FarmerCropIntention i1 = new FarmerCropIntention(farmerUser, "Andhra Pradesh", "Guntur", maizeCrop, Season.KHARIF, 5.0);
        FarmerCropIntention i2 = new FarmerCropIntention(farmerUser, "Andhra Pradesh", "Guntur", maizeCrop, Season.KHARIF, 3.0);
        FarmerCropIntention i3 = new FarmerCropIntention(farmerUser, "Andhra Pradesh", "Guntur", riceCrop, Season.KHARIF, 2.0);

        when(farmerCropIntentionRepository.findRegionalIntentions("Andhra Pradesh", "Guntur", Season.KHARIF))
                .thenReturn(List.of(i1, i2, i3));
        when(cropRepository.findByActiveTrue()).thenReturn(List.of(maizeCrop, riceCrop));

        OversupplyRiskResponseDto riskResponse = farmerCropIntentionService.getOversupplyRisk("Andhra Pradesh", "Guntur", Season.KHARIF);

        assertNotNull(riskResponse);
        assertFalse(riskResponse.getDataSufficient(), "Data sufficiency must be FALSE because only 1 distinct farmer is present");
        assertEquals(3, riskResponse.getTotalRegionalIntentions());
        assertEquals(1, riskResponse.getTotalDistinctFarmers(), "Distinct farmer count must be exactly 1");
        assertTrue(riskResponse.getSummaryMessage().contains("1 distinct farmer"));
        assertTrue(riskResponse.getSummaryMessage().contains("minimum of 3 distinct farmers is required"));

        for (OversupplyRiskItemDto item : riskResponse.getCropRiskItems()) {
            assertEquals("INSUFFICIENT_DATA", item.getRiskLevel());
            assertEquals("Insufficient Data", item.getRiskLabel());
            assertTrue(item.getExplanation().contains("Sample size is limited"));
        }
    }

    @Test
    @DisplayName("Three distinct farmers satisfying the minimum sample-size rule classifies risk properly")
    void getOversupplyRisk_ThreeDistinctFarmers_SufficientData() {
        authenticate(farmerUser);

        // 3 distinct farmers (ids 10, 20, 30)
        FarmerCropIntention i1 = new FarmerCropIntention(farmerUser, "Andhra Pradesh", "Guntur", maizeCrop, Season.KHARIF, 10.0);
        FarmerCropIntention i2 = new FarmerCropIntention(otherUser, "Andhra Pradesh", "Guntur", maizeCrop, Season.KHARIF, 10.0);
        FarmerCropIntention i3 = new FarmerCropIntention(thirdUser, "Andhra Pradesh", "Guntur", riceCrop, Season.KHARIF, 5.0);

        when(farmerCropIntentionRepository.findRegionalIntentions("Andhra Pradesh", "Guntur", Season.KHARIF))
                .thenReturn(List.of(i1, i2, i3));
        when(cropRepository.findByActiveTrue()).thenReturn(List.of(maizeCrop, riceCrop));

        OversupplyRiskResponseDto riskResponse = farmerCropIntentionService.getOversupplyRisk("Andhra Pradesh", "Guntur", Season.KHARIF);

        assertNotNull(riskResponse);
        assertTrue(riskResponse.getDataSufficient(), "Data sufficiency must be TRUE for 3 distinct farmers");
        assertEquals(3, riskResponse.getTotalRegionalIntentions());
        assertEquals(3, riskResponse.getTotalDistinctFarmers());
        assertEquals(25.0, riskResponse.getTotalRegionalArea());

        OversupplyRiskItemDto maizeItem = riskResponse.getCropRiskItems().stream()
                .filter(item -> item.getCropId().equals(1L))
                .findFirst().orElseThrow();

        // 2 intentions for Maize out of 3 = 66.7% share -> HIGH
        // 20 acres out of 25 = 80.0% area share -> HIGH
        assertEquals("HIGH", maizeItem.getRiskLevel());
        assertEquals("High Oversupply Risk", maizeItem.getRiskLabel());
        assertEquals(66.7, maizeItem.getIntentionShare());
        assertEquals(80.0, maizeItem.getAreaShare());
    }

    @Test
    @DisplayName("getOversupplyRisk isolates seasons properly")
    void getOversupplyRisk_SeasonIsolation() {
        authenticate(farmerUser);

        when(farmerCropIntentionRepository.findRegionalIntentions("Telangana", "Nalgonda", Season.RABI))
                .thenReturn(Collections.emptyList());
        when(cropRepository.findByActiveTrue()).thenReturn(List.of(maizeCrop));

        OversupplyRiskResponseDto riskResponse = farmerCropIntentionService.getOversupplyRisk("Telangana", "Nalgonda", Season.RABI);

        assertNotNull(riskResponse);
        assertEquals(Season.RABI, riskResponse.getSeason());
        verify(farmerCropIntentionRepository, times(1)).findRegionalIntentions("Telangana", "Nalgonda", Season.RABI);
        verify(farmerCropIntentionRepository, never()).findRegionalIntentions("Telangana", "Nalgonda", Season.KHARIF);
    }

    @Test
    @DisplayName("getOversupplyRisk calculates correct intention and area shares after aggregation")
    void getOversupplyRisk_CalculatesCorrectShares() {
        authenticate(farmerUser);

        // 3 distinct farmers
        // Maize: 1 farmer, 20.0 acres
        // Rice: 2 farmers, 30.0 acres (15.0 + 15.0)
        // Total intentions = 3, Total area = 50.0 acres
        FarmerCropIntention i1 = new FarmerCropIntention(farmerUser, "Punjab", "Ludhiana", maizeCrop, Season.KHARIF, 20.0);
        FarmerCropIntention i2 = new FarmerCropIntention(otherUser, "Punjab", "Ludhiana", riceCrop, Season.KHARIF, 15.0);
        FarmerCropIntention i3 = new FarmerCropIntention(thirdUser, "Punjab", "Ludhiana", riceCrop, Season.KHARIF, 15.0);

        when(farmerCropIntentionRepository.findRegionalIntentions("Punjab", "Ludhiana", Season.KHARIF))
                .thenReturn(List.of(i1, i2, i3));
        when(cropRepository.findByActiveTrue()).thenReturn(List.of(maizeCrop, riceCrop));

        OversupplyRiskResponseDto response = farmerCropIntentionService.getOversupplyRisk("Punjab", "Ludhiana", Season.KHARIF);

        assertNotNull(response);
        assertTrue(response.getDataSufficient());
        assertEquals(3, response.getTotalDistinctFarmers());
        assertEquals(3, response.getTotalRegionalIntentions());
        assertEquals(50.0, response.getTotalRegionalArea());

        OversupplyRiskItemDto riceItem = response.getCropRiskItems().stream()
                .filter(item -> item.getCropId().equals(2L))
                .findFirst().orElseThrow();

        // Rice: 2 / 3 intentions = 66.7%, 30.0 / 50.0 area = 60.0% -> max is 66.7% >= 45% -> HIGH
        assertEquals(2, riceItem.getFarmerIntentionCount());
        assertEquals(30.0, riceItem.getIntendedArea());
        assertEquals(66.7, riceItem.getIntentionShare());
        assertEquals(60.0, riceItem.getAreaShare());
        assertEquals("HIGH", riceItem.getRiskLevel());

        OversupplyRiskItemDto maizeItem = response.getCropRiskItems().stream()
                .filter(item -> item.getCropId().equals(1L))
                .findFirst().orElseThrow();

        // Maize: 1 / 3 intentions = 33.3%, 20.0 / 50.0 area = 40.0% -> max is 40.0% (between 25% and 45%) -> MODERATE
        assertEquals(1, maizeItem.getFarmerIntentionCount());
        assertEquals(20.0, maizeItem.getIntendedArea());
        assertEquals(33.3, maizeItem.getIntentionShare());
        assertEquals(40.0, maizeItem.getAreaShare());
        assertEquals("MODERATE", maizeItem.getRiskLevel());
    }

    @Test
    @DisplayName("getOversupplyRisk handles empty regional data gracefully")
    void getOversupplyRisk_EmptyData() {
        authenticate(farmerUser);

        when(farmerCropIntentionRepository.findRegionalIntentions("Maharashtra", "Nashik", Season.KHARIF))
                .thenReturn(Collections.emptyList());
        when(cropRepository.findByActiveTrue()).thenReturn(List.of(maizeCrop, riceCrop));

        OversupplyRiskResponseDto response = farmerCropIntentionService.getOversupplyRisk("Maharashtra", "Nashik", Season.KHARIF);

        assertNotNull(response);
        assertFalse(response.getDataSufficient());
        assertEquals(0, response.getTotalRegionalIntentions());
        assertEquals(0, response.getTotalDistinctFarmers());
        assertEquals(0.0, response.getTotalRegionalArea());
        assertTrue(response.getSummaryMessage().contains("0 distinct farmers"));

        for (OversupplyRiskItemDto item : response.getCropRiskItems()) {
            assertEquals(0, item.getFarmerIntentionCount());
            assertEquals(0.0, item.getIntendedArea());
            assertEquals(0.0, item.getIntentionShare());
            assertEquals(0.0, item.getAreaShare());
            assertEquals("INSUFFICIENT_DATA", item.getRiskLevel());
        }
    }
}
