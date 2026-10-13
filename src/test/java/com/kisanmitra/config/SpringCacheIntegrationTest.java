package com.kisanmitra.config;

import com.kisanmitra.dto.GovernmentSchemeDto;
import com.kisanmitra.dto.GovernmentSchemeRequestDto;
import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.entity.SchemeCategory;
import com.kisanmitra.repository.GovernmentSchemeRepository;
import com.kisanmitra.service.GovernmentSchemeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SpringCacheIntegrationTest {

    @Autowired
    private GovernmentSchemeService governmentSchemeService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private GovernmentSchemeRepository schemeRepository;

    @BeforeEach
    void clearCaches() {
        if (cacheManager.getCache(CacheConfig.CACHE_GOVERNMENT_SCHEMES) != null) {
            cacheManager.getCache(CacheConfig.CACHE_GOVERNMENT_SCHEMES).clear();
        }
        if (cacheManager.getCache(CacheConfig.CACHE_GOVERNMENT_SCHEME_BY_ID) != null) {
            cacheManager.getCache(CacheConfig.CACHE_GOVERNMENT_SCHEME_BY_ID).clear();
        }
    }

    @Test
    @DisplayName("1. Cache hit: Repeated calls to searchSchemes with same parameters reuse cache without invoking repository")
    void testSearchSchemesCacheHit() {
        GovernmentScheme scheme = new GovernmentScheme(
                "PM-KISAN", SchemeCategory.FINANCIAL_SUPPORT, "Ministry of Agriculture",
                "Income support", "Small farmers", "Rs. 6000/yr", "Aadhaar",
                "Online", "https://pmkisan.gov.in", "Govt Portal", "2026-10-10", "VERIFIED"
        );
        scheme.setId(101L);

        when(schemeRepository.searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "kisan"))
                .thenReturn(List.of(scheme));

        // First call - invokes repository (cache miss)
        List<GovernmentSchemeDto> firstResult = governmentSchemeService.searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "kisan");
        assertThat(firstResult).hasSize(1);
        verify(schemeRepository, times(1)).searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "kisan");

        // Second call with same arguments - served from Caffeine cache (cache hit)
        List<GovernmentSchemeDto> secondResult = governmentSchemeService.searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "kisan");
        assertThat(secondResult).hasSize(1);
        // Repository should still have been called only once!
        verify(schemeRepository, times(1)).searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "kisan");
    }

    @Test
    @DisplayName("2. Separate parameter keys: Distinct queries are stored in separate cache keys")
    void testSeparateParameterKeys() {
        GovernmentScheme financialScheme = new GovernmentScheme(
                "PM-KISAN", SchemeCategory.FINANCIAL_SUPPORT, "Ministry of Agriculture",
                "Income support", "Small farmers", "Rs. 6000/yr", "Aadhaar",
                "Online", "https://pmkisan.gov.in", "Govt Portal", "2026-10-10", "VERIFIED"
        );
        financialScheme.setId(101L);

        GovernmentScheme insuranceScheme = new GovernmentScheme(
                "PMFBY", SchemeCategory.CROP_INSURANCE, "Ministry of Agriculture",
                "Crop insurance", "Farmers", "Crop loss coverage", "Land records",
                "Portal", "https://pmfby.gov.in", "Govt Portal", "2026-10-10", "VERIFIED"
        );
        insuranceScheme.setId(102L);

        when(schemeRepository.searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "kisan")).thenReturn(List.of(financialScheme));
        when(schemeRepository.searchSchemes(SchemeCategory.CROP_INSURANCE, "pmfby")).thenReturn(List.of(insuranceScheme));

        // Query 1
        List<GovernmentSchemeDto> res1 = governmentSchemeService.searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "kisan");
        assertThat(res1).hasSize(1);
        assertThat(res1.get(0).getName()).isEqualTo("PM-KISAN");

        // Query 2 (different parameters)
        List<GovernmentSchemeDto> res2 = governmentSchemeService.searchSchemes(SchemeCategory.CROP_INSURANCE, "pmfby");
        assertThat(res2).hasSize(1);
        assertThat(res2.get(0).getName()).isEqualTo("PMFBY");

        verify(schemeRepository, times(1)).searchSchemes(SchemeCategory.FINANCIAL_SUPPORT, "kisan");
        verify(schemeRepository, times(1)).searchSchemes(SchemeCategory.CROP_INSURANCE, "pmfby");
    }

    @Test
    @DisplayName("3. Cache invalidation: Updating or creating a scheme evicts the cache")
    void testCacheEvictionOnMutation() {
        GovernmentScheme scheme = new GovernmentScheme(
                "PM-KISAN", SchemeCategory.FINANCIAL_SUPPORT, "Ministry of Agriculture",
                "Income support", "Small farmers", "Rs. 6000/yr", "Aadhaar",
                "Online", "https://pmkisan.gov.in", "Govt Portal", "2026-10-10", "VERIFIED"
        );
        scheme.setId(201L);

        when(schemeRepository.findById(201L)).thenReturn(Optional.of(scheme));
        when(schemeRepository.save(any(GovernmentScheme.class))).thenReturn(scheme);

        // 1. Initial read caches the item
        GovernmentSchemeDto dto1 = governmentSchemeService.getSchemeById(201L);
        assertThat(dto1.getName()).isEqualTo("PM-KISAN");
        verify(schemeRepository, times(1)).findById(201L);

        // 2. Second read hits cache
        GovernmentSchemeDto dto2 = governmentSchemeService.getSchemeById(201L);
        assertThat(dto2.getName()).isEqualTo("PM-KISAN");
        verify(schemeRepository, times(1)).findById(201L);

        // 3. Mutation occurs (updateScheme) -> triggers @CacheEvict
        GovernmentSchemeRequestDto updateDto = new GovernmentSchemeRequestDto();
        updateDto.setName("PM-KISAN Updated");
        updateDto.setCategory(SchemeCategory.FINANCIAL_SUPPORT);
        updateDto.setMinistry("Ministry of Agriculture");
        updateDto.setPurpose("Updated purpose");
        updateDto.setEligibleApplicants("Small farmers");
        updateDto.setBenefits("Rs. 6000/yr");
        updateDto.setRequiredDocuments("Aadhaar");
        updateDto.setApplicationProcess("Online");
        updateDto.setOfficialPortalUrl("https://pmkisan.gov.in");
        updateDto.setSourceName("Govt Portal");

        governmentSchemeService.updateScheme(201L, updateDto);

        // 4. Subsequent read must miss cache and query repository again
        governmentSchemeService.getSchemeById(201L);
        verify(schemeRepository, times(3)).findById(201L); // 1 initial + 1 in updateScheme + 1 after eviction
    }
}
