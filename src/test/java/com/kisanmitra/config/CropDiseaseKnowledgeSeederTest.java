package com.kisanmitra.config;

import com.kisanmitra.entity.CropDiseaseKnowledge;
import com.kisanmitra.repository.CropDiseaseKnowledgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CropDiseaseKnowledgeSeederTest {

    @Mock
    private CropDiseaseKnowledgeRepository knowledgeRepository;

    private CropDiseaseKnowledgeSeeder seeder;

    @BeforeEach
    void setUp() {
        seeder = new CropDiseaseKnowledgeSeeder(knowledgeRepository);
    }

    @Test
    @DisplayName("run should seed 8 disease entries when repository is empty")
    void run_SeedsKnowledgeWhenEmpty() {
        when(knowledgeRepository.count()).thenReturn(0L);

        seeder.run();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CropDiseaseKnowledge>> captor = ArgumentCaptor.forClass(List.class);
        verify(knowledgeRepository, times(1)).saveAll(captor.capture());

        List<CropDiseaseKnowledge> savedList = captor.getValue();
        assertEquals(8, savedList.size());

        long verifiedCount = savedList.stream()
                .filter(k -> "VERIFIED".equalsIgnoreCase(k.getVerificationStatus()))
                .count();

        long unverifiedCount = savedList.stream()
                .filter(k -> "VERIFICATION_REQUIRED".equalsIgnoreCase(k.getVerificationStatus()))
                .count();

        assertEquals(7, verifiedCount, "Exactly 7 disease entries must be fully VERIFIED by ICAR literature");
        assertEquals(1, unverifiedCount, "Exactly 1 disease entry must be marked VERIFICATION_REQUIRED");

        // Verify source title and URL presence for all saved entries
        for (CropDiseaseKnowledge k : savedList) {
            assertNotNull(k.getSourceName(), "Source name/title must be present for " + k.getDiseaseName());
            assertNotNull(k.getSourceUrl(), "Source URL must be a valid link for " + k.getDiseaseName());
            assertNotNull(k.getLastVerifiedDate(), "Last verified date must be present for " + k.getDiseaseName());
            assertTrue(k.getSourceUrl().startsWith("http"), "Source URL must be a valid HTTP/HTTPS link");
        }

        assertFalse(savedList.stream().anyMatch(k -> k.getNonChemicalManagement() != null && k.getNonChemicalManagement().contains("@")),
                "Seeded disease records must not contain unsupported dosage-style treatment strings");
        assertFalse(savedList.stream().anyMatch(k -> k.getNonChemicalManagement() != null && k.getNonChemicalManagement().contains("%")),
                "Seeded disease records must not include specific unsupported concentration claims");
    }

    @Test
    @DisplayName("run should skip seeding when repository already contains entries")
    void run_SkipsWhenNotEmpty() {
        when(knowledgeRepository.count()).thenReturn(8L);

        seeder.run();

        verify(knowledgeRepository, never()).saveAll(any());
    }
}
