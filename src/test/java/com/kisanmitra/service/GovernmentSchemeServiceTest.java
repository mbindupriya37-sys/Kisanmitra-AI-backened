package com.kisanmitra.service;

import com.kisanmitra.dto.GovernmentSchemeDto;
import com.kisanmitra.dto.GovernmentSchemeRequestDto;
import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.entity.SchemeCategory;
import com.kisanmitra.exception.ResourceNotFoundException;
import com.kisanmitra.repository.GovernmentSchemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GovernmentSchemeServiceTest {

    @Mock
    private GovernmentSchemeRepository schemeRepository;

    private GovernmentSchemeService schemeService;

    @BeforeEach
    void setUp() {
        schemeService = new GovernmentSchemeService(schemeRepository);
    }

    @Test
    @DisplayName("searchSchemes should return DTO list matching search query")
    void searchSchemes_Success() {
        GovernmentScheme scheme = new GovernmentScheme(
                "PM-KISAN", SchemeCategory.FINANCIAL_SUPPORT, "Ministry of Agriculture",
                "Income support", "Small farmers", "₹6000/yr", "Aadhaar", "Online",
                "https://pmkisan.gov.in", "Govt of India", "2026-10-08", "VERIFIED"
        );
        scheme.setId(1L);

        when(schemeRepository.searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "KISAN"))
                .thenReturn(List.of(scheme));

        List<GovernmentSchemeDto> results = schemeService.searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "KISAN");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("PM-KISAN", results.get(0).getName());
        assertEquals("Financial Support", results.get(0).getCategoryDisplayName());
    }

    @Test
    @DisplayName("getSchemeById should return scheme DTO when found")
    void getSchemeById_Success() {
        GovernmentScheme scheme = new GovernmentScheme(
                "PMFBY", SchemeCategory.CROP_INSURANCE, "Ministry of Agriculture",
                "Crop insurance", "Farmers", "Subsidized premium", "Land record", "Portal",
                "https://pmfby.gov.in", "MyScheme", "2026-10-08", "VERIFIED"
        );
        scheme.setId(2L);

        when(schemeRepository.findById(2L)).thenReturn(Optional.of(scheme));

        GovernmentSchemeDto dto = schemeService.getSchemeById(2L);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("PMFBY", dto.getName());
    }

    @Test
    @DisplayName("getSchemeById should throw ResourceNotFoundException when not found")
    void getSchemeById_NotFound() {
        when(schemeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> schemeService.getSchemeById(99L));
    }

    @Test
    @DisplayName("createScheme should persist and return new scheme DTO")
    void createScheme_Success() {
        GovernmentSchemeRequestDto req = new GovernmentSchemeRequestDto();
        req.setName("KCC Scheme");
        req.setCategory(SchemeCategory.FINANCIAL_SUPPORT);
        req.setMinistry("Ministry of Agriculture");
        req.setPurpose("Credit support");
        req.setEligibleApplicants("Farmers");
        req.setBenefits("4% interest rate");
        req.setRequiredDocuments("KCC form, Aadhaar");
        req.setApplicationProcess("Bank branch");
        req.setOfficialPortalUrl("https://myscheme.gov.in");
        req.setSourceName("MyScheme");

        GovernmentScheme saved = new GovernmentScheme(
                req.getName(), req.getCategory(), req.getMinistry(), req.getPurpose(),
                req.getEligibleApplicants(), req.getBenefits(), req.getRequiredDocuments(),
                req.getApplicationProcess(), req.getOfficialPortalUrl(), req.getSourceName(),
                "2026-10-08", "VERIFIED"
        );
        saved.setId(3L);

        when(schemeRepository.save(any(GovernmentScheme.class))).thenReturn(saved);

        GovernmentSchemeDto dto = schemeService.createScheme(req);

        assertNotNull(dto);
        assertEquals(3L, dto.getId());
        assertEquals("KCC Scheme", dto.getName());
    }
}
