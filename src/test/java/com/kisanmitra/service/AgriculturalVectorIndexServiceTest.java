package com.kisanmitra.service;

import com.kisanmitra.dto.AgriculturalKnowledgeChunk;
import com.kisanmitra.dto.SemanticSearchResultDto;
import com.kisanmitra.entity.Crop;
import com.kisanmitra.entity.CropDiseaseKnowledge;
import com.kisanmitra.entity.CropRequirement;
import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.entity.SchemeCategory;
import com.kisanmitra.repository.CropDiseaseKnowledgeRepository;
import com.kisanmitra.repository.CropRequirementRepository;
import com.kisanmitra.repository.GovernmentSchemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgriculturalVectorIndexServiceTest {

    @Mock
    private CropDiseaseKnowledgeRepository diseaseRepository;

    @Mock
    private GovernmentSchemeRepository schemeRepository;

    @Mock
    private CropRequirementRepository cropRequirementRepository;

    private VectorEmbeddingService embeddingService;
    private AgriculturalVectorIndexService indexService;

    @BeforeEach
    void setUp() {
        embeddingService = new VectorEmbeddingService();
        indexService = new InMemoryAgriculturalVectorIndexService(
                embeddingService, diseaseRepository, schemeRepository, cropRequirementRepository
        );
    }

    @Test
    @DisplayName("initializeIndex should populate vector index from repositories and baseline chunks")
    void testInitializeIndex() {
        CropDiseaseKnowledge disease = new CropDiseaseKnowledge(
                "Rice", "Blast", "Pyricularia oryzae", "Leaves",
                "Spindle-shaped lesions with gray centers", "Use Trichoderma harzianum seed treatment",
                "Avoid excessive nitrogen fertilization", "Consult local KVK for bioagent dosage",
                "ICAR - CRRI", "https://crri.icar.gov.in", "2024-01-01", "VERIFIED"
        );
        disease.setId(1L);

        GovernmentScheme scheme = new GovernmentScheme(
                "PM-KISAN", SchemeCategory.FINANCIAL_SUPPORT, "Ministry of Agriculture",
                "Income support of 6000 per year", "Small and marginal farmers", "Direct bank transfer",
                "Aadhaar, Land records", "Apply online", "https://pmkisan.gov.in", "PM-KISAN Portal",
                "2024-01-01", "VERIFIED"
        );
        scheme.setId(2L);

        Crop crop = new Crop();
        crop.setId(3L);
        crop.setName("Wheat");

        CropRequirement req = new CropRequirement();
        req.setId(4L);
        req.setCrop(crop);
        req.setMinSoilPh(6.0);
        req.setMaxSoilPh(7.5);
        req.setWaterRequirementDescription("Medium water availability with timely crown root irrigation");
        req.setSourceName("ICAR Agronomy Guidelines");
        req.setSourceUrl("https://icar.org.in");
        req.setActive(true);

        when(diseaseRepository.findAll()).thenReturn(List.of(disease));
        when(schemeRepository.findAll()).thenReturn(List.of(scheme));
        when(cropRequirementRepository.findAll()).thenReturn(List.of(req));

        indexService.initializeIndex();

        // only repository-backed chunks are indexed; no synthetic production market/weather fixtures should be injected
        assertEquals(3, indexService.getIndexedChunkCount());
    }

    @Test
    @DisplayName("search should rank the most relevant semantic chunk first with descending similarity scores")
    void testSearchRanking() {
        float[] blastVec = embeddingService.generateEmbedding("Rice blast fungal disease symptoms lesions Pyricularia");
        AgriculturalKnowledgeChunk chunk1 = new AgriculturalKnowledgeChunk(
                "CHUNK_BLAST", "DISEASE", "Rice Blast",
                "Rice blast symptoms include spindle-shaped lesions on leaves.",
                "ICAR-CRRI", "https://crri.icar.gov.in", blastVec
        );

        float[] schemeVec = embeddingService.generateEmbedding("PM-KISAN financial assistance income support subsidy");
        AgriculturalKnowledgeChunk chunk2 = new AgriculturalKnowledgeChunk(
                "CHUNK_SCHEME", "SCHEME", "PM-KISAN",
                "PM-KISAN provides Rs 6,000 yearly benefit to eligible landholding farmers.",
                "Ministry of Agriculture", "https://pmkisan.gov.in", schemeVec
        );

        indexService.indexChunk(chunk1);
        indexService.indexChunk(chunk2);

        List<SemanticSearchResultDto> results = indexService.search("rice blast fungus leaf lesions", 2, 0.20f);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals("CHUNK_BLAST", results.get(0).getChunk().getId());
        assertTrue(results.get(0).getSimilarityScore() > 0.40f);

        if (results.size() > 1) {
            assertTrue(results.get(0).getSimilarityScore() >= results.get(1).getSimilarityScore(),
                    "Results must be sorted descending by similarity");
        }
    }

    @Test
    @DisplayName("search should reject low-similarity out-of-domain queries and return an empty list")
    void testSearchThresholdFiltering() {
        float[] riceVec = embeddingService.generateEmbedding("Rice agronomy soil pH nitrogen fertilizer");
        AgriculturalKnowledgeChunk chunk = new AgriculturalKnowledgeChunk(
                "CHUNK_RICE", "CROP_REQUIREMENT", "Rice Agronomy",
                "Rice agronomy requires soil pH between 5.5 and 7.0.",
                "ICAR", "https://icar.org.in", riceVec
        );
        indexService.indexChunk(chunk);

        // Completely unrelated query with no agricultural overlap
        List<SemanticSearchResultDto> results = indexService.search(
                "quantum mechanics cryptography telescope astrophysics", 3, 0.35f
        );

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Out-of-domain query should return empty list under threshold 0.35f");
    }

    @Test
    @DisplayName("search should handle empty, null, and whitespace inputs gracefully")
    void testSearchEmptyInputs() {
        indexService.indexChunk(new AgriculturalKnowledgeChunk(
                "CHUNK_1", "DISEASE", "Rust", "Wheat rust", "ICAR", "https://icar.org.in",
                embeddingService.generateEmbedding("Wheat rust")
        ));

        assertTrue(indexService.search(null, 3, 0.30f).isEmpty());
        assertTrue(indexService.search("", 3, 0.30f).isEmpty());
        assertTrue(indexService.search("    ", 3, 0.30f).isEmpty());
        assertTrue(indexService.search("how what where why", 3, 0.30f).isEmpty()); // Stopwords only
    }

    @Test
    @DisplayName("search against an empty vector index should return an empty list without error")
    void testSearchEmptyIndex() {
        indexService.clearIndex();
        assertEquals(0, indexService.getIndexedChunkCount());

        List<SemanticSearchResultDto> results = indexService.search("rice disease", 3, 0.30f);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Retrieved search results must preserve truthful source attribution and metadata")
    void testChunkAttributionPreserved() {
        float[] vec = embeddingService.generateEmbedding("Groundnut tikka disease leaf spot cercospora");
        AgriculturalKnowledgeChunk chunk = new AgriculturalKnowledgeChunk(
                "DISEASE_101", "DISEASE", "Groundnut Tikka Disease",
                "Tikka disease causes circular dark spots on groundnut foliage.",
                "ICAR - Directorate of Groundnut Research",
                "https://dgr.icar.gov.in",
                vec
        );
        indexService.indexChunk(chunk);

        List<SemanticSearchResultDto> results = indexService.search("groundnut tikka disease", 1, 0.30f);

        assertEquals(1, results.size());
        AgriculturalKnowledgeChunk retrieved = results.get(0).getChunk();
        assertEquals("DISEASE_101", retrieved.getId());
        assertEquals("ICAR - Directorate of Groundnut Research", retrieved.getSourceName());
        assertEquals("https://dgr.icar.gov.in", retrieved.getSourceUrl());
        assertEquals("DISEASE", retrieved.getCategory());
    }
}
