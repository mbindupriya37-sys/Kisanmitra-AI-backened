package com.kisanmitra.service;

import com.kisanmitra.dto.AgriculturalKnowledgeChunk;
import com.kisanmitra.dto.SemanticSearchResultDto;
import com.kisanmitra.entity.CropDiseaseKnowledge;
import com.kisanmitra.entity.CropRequirement;
import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.repository.CropDiseaseKnowledgeRepository;
import com.kisanmitra.repository.CropRequirementRepository;
import com.kisanmitra.repository.GovernmentSchemeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Thread-safe In-Memory Vector Search Index implementation for Agricultural Knowledge Chunks.
 * Precomputes 128-dimensional L2-normalized semantic embeddings for domain knowledge
 * and performs cosine similarity top-K retrieval with confidence thresholding.
 */
@Service("agriculturalVectorIndexService")
public class InMemoryAgriculturalVectorIndexService implements AgriculturalVectorIndexService {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryAgriculturalVectorIndexService.class);

    private final VectorEmbeddingService embeddingService;
    private final CropDiseaseKnowledgeRepository diseaseRepository;
    private final GovernmentSchemeRepository schemeRepository;
    private final CropRequirementRepository cropRequirementRepository;
    private final TransactionTemplate transactionTemplate;

    private final List<AgriculturalKnowledgeChunk> index = new CopyOnWriteArrayList<>();

    @Autowired
    public InMemoryAgriculturalVectorIndexService(VectorEmbeddingService embeddingService,
                                                 CropDiseaseKnowledgeRepository diseaseRepository,
                                                 GovernmentSchemeRepository schemeRepository,
                                                 CropRequirementRepository cropRequirementRepository,
                                                 @Autowired(required = false) PlatformTransactionManager transactionManager) {
        this.embeddingService = embeddingService;
        this.diseaseRepository = diseaseRepository;
        this.schemeRepository = schemeRepository;
        this.cropRequirementRepository = cropRequirementRepository;
        this.transactionTemplate = transactionManager != null ? new TransactionTemplate(transactionManager) : null;
    }

    public InMemoryAgriculturalVectorIndexService(VectorEmbeddingService embeddingService,
                                                 CropDiseaseKnowledgeRepository diseaseRepository,
                                                 GovernmentSchemeRepository schemeRepository,
                                                 CropRequirementRepository cropRequirementRepository) {
        this(embeddingService, diseaseRepository, schemeRepository, cropRequirementRepository, null);
    }

    @Override
    @PostConstruct
    public void initializeIndex() {
        if (transactionTemplate != null) {
            try {
                transactionTemplate.execute(status -> {
                    doInitializeIndex();
                    return null;
                });
            } catch (Exception e) {
                logger.warn("Transactional vector index initialization encountered error: {}. Index size: {}", e.getMessage(), index.size());
            }
        } else {
            doInitializeIndex();
        }
    }

    private void doInitializeIndex() {
        try {
            logger.info("Initializing KisanMitra in-memory agricultural vector index...");
            index.clear();

            // 1. Index Crop Disease Knowledge
            if (diseaseRepository != null) {
                List<CropDiseaseKnowledge> diseases = diseaseRepository.findAll();
                for (CropDiseaseKnowledge d : diseases) {
                    if ("VERIFIED".equalsIgnoreCase(d.getVerificationStatus())) {
                        String textToEmbed = String.join(" ",
                                d.getCropName(),
                                d.getDiseaseName(),
                                d.getScientificName(),
                                d.getAffectedPlantParts() != null ? d.getAffectedPlantParts() : "",
                                d.getSymptomsDescription() != null ? d.getSymptomsDescription() : "",
                                d.getNonChemicalManagement() != null ? d.getNonChemicalManagement() : "",
                                d.getCulturalPractices() != null ? d.getCulturalPractices() : "",
                                d.getKvkExtensionAdvisory() != null ? d.getKvkExtensionAdvisory() : ""
                        );

                        float[] embedding = embeddingService.generateEmbedding(textToEmbed);

                        StringBuilder content = new StringBuilder();
                        content.append("🦠 **Disease Record [").append(d.getCropName()).append(" - ").append(d.getDiseaseName()).append("]:**\n");
                        content.append("- Scientific Name: *").append(d.getScientificName()).append("*\n");
                        if (d.getAffectedPlantParts() != null) {
                            content.append("- Affected Parts: ").append(d.getAffectedPlantParts()).append("\n");
                        }
                        content.append("- Symptoms: ").append(d.getSymptomsDescription()).append("\n");
                        content.append("- Non-Chemical Control: ").append(d.getNonChemicalManagement()).append("\n");
                        content.append("- Cultural Practices: ").append(d.getCulturalPractices()).append("\n");
                        if (d.getKvkExtensionAdvisory() != null && !d.getKvkExtensionAdvisory().isEmpty()) {
                            content.append("- KVK Extension Advisory: ").append(d.getKvkExtensionAdvisory()).append("\n");
                        }

                        Map<String, String> meta = new HashMap<>();
                        meta.put("crop", d.getCropName());
                        meta.put("disease", d.getDiseaseName());

                        index.add(new AgriculturalKnowledgeChunk(
                                "DISEASE_" + d.getId(),
                                "DISEASE",
                                d.getCropName() + " - " + d.getDiseaseName(),
                                content.toString(),
                                d.getSourceName(),
                                d.getSourceUrl(),
                                embedding,
                                meta
                        ));
                    }
                }
            }

            // 2. Index Government Schemes
            if (schemeRepository != null) {
                List<GovernmentScheme> schemes = schemeRepository.findAll();
                for (GovernmentScheme s : schemes) {
                    if ("VERIFIED".equalsIgnoreCase(s.getVerificationStatus())) {
                        String textToEmbed = String.join(" ",
                                s.getName(),
                                s.getCategory() != null ? s.getCategory().name() : "",
                                s.getMinistry() != null ? s.getMinistry() : "",
                                s.getPurpose() != null ? s.getPurpose() : "",
                                s.getEligibleApplicants() != null ? s.getEligibleApplicants() : "",
                                s.getBenefits() != null ? s.getBenefits() : "",
                                "subsidy financial assistance grant portal"
                        );

                        float[] embedding = embeddingService.generateEmbedding(textToEmbed);

                        StringBuilder content = new StringBuilder();
                        content.append("🏛️ **Government Scheme [").append(s.getName()).append("]:**\n");
                        content.append("- Category: ").append(s.getCategory()).append(" (").append(s.getMinistry()).append(")\n");
                        content.append("- Purpose: ").append(s.getPurpose()).append("\n");
                        content.append("- Eligible Applicants: ").append(s.getEligibleApplicants()).append("\n");
                        content.append("- Key Benefits: ").append(s.getBenefits()).append("\n");
                        content.append("- Application Portal: ").append(s.getOfficialPortalUrl()).append("\n");

                        Map<String, String> meta = new HashMap<>();
                        meta.put("scheme", s.getName());

                        index.add(new AgriculturalKnowledgeChunk(
                                "SCHEME_" + s.getId(),
                                "SCHEME",
                                s.getName(),
                                content.toString(),
                                s.getSourceName(),
                                s.getOfficialPortalUrl(),
                                embedding,
                                meta
                        ));
                    }
                }
            }

            // 3. Index Crop Requirements
            if (cropRequirementRepository != null) {
                List<CropRequirement> requirements = cropRequirementRepository.findAll();
                for (CropRequirement req : requirements) {
                    if (req.getCrop() != null && Boolean.TRUE.equals(req.getActive())) {
                        String cropName = req.getCrop().getName();
                        String textToEmbed = String.join(" ",
                                cropName,
                                "agronomic requirement soil ph temperature rainfall water irrigation cultivation",
                                req.getWaterRequirementDescription() != null ? req.getWaterRequirementDescription() : "",
                                req.getGrowingPeriodDescription() != null ? req.getGrowingPeriodDescription() : "",
                                req.getSustainabilityNotes() != null ? req.getSustainabilityNotes() : ""
                        );

                        float[] embedding = embeddingService.generateEmbedding(textToEmbed);

                        StringBuilder content = new StringBuilder();
                        content.append("🌱 **Agronomic Requirements [").append(cropName).append("]:**\n");
                        content.append("- Temperature Range: ").append(req.getMinTemperatureRange()).append(" - ").append(req.getMaxTemperatureRange()).append("\n");
                        content.append("- Ideal Soil pH: ").append(req.getMinSoilPh()).append(" - ").append(req.getMaxSoilPh()).append("\n");
                        content.append("- Rainfall Range: ").append(req.getMinRainfall()).append(" - ").append(req.getMaxRainfall()).append("\n");
                        if (req.getWaterRequirementDescription() != null) {
                            content.append("- Water Description: ").append(req.getWaterRequirementDescription()).append("\n");
                        }
                        if (req.getSustainabilityNotes() != null) {
                            content.append("- Sustainability Notes: ").append(req.getSustainabilityNotes()).append("\n");
                        }

                        String sourceName = req.getSourceName() != null ? req.getSourceName() : "ICAR Agronomy Handbook";
                        String sourceUrl = req.getSourceUrl() != null ? req.getSourceUrl() : "https://icar.org.in";

                        Map<String, String> meta = new HashMap<>();
                        meta.put("crop", cropName);

                        index.add(new AgriculturalKnowledgeChunk(
                                "CROP_REQ_" + req.getId(),
                                "CROP_REQUIREMENT",
                                "Agronomic Requirements for " + cropName,
                                content.toString(),
                                sourceName,
                                sourceUrl,
                                embedding,
                                meta
                        ));
                    }
                }
            }

            logger.info("KisanMitra in-memory vector index initialized successfully with {} knowledge chunks.", index.size());
        } catch (Exception e) {
            logger.warn("Vector index initialization encountered non-fatal error: {}. Index size: {}", e.getMessage(), index.size());
        }
    }

    @Override
    public List<SemanticSearchResultDto> search(String queryText, int topK, float minSimilarityThreshold) {
        if (queryText == null || queryText.trim().isEmpty() || index.isEmpty() || topK <= 0) {
            return Collections.emptyList();
        }

        float[] queryEmbedding = embeddingService.generateEmbedding(queryText);

        boolean hasMagnitude = false;
        for (float val : queryEmbedding) {
            if (Math.abs(val) > 1e-6) {
                hasMagnitude = true;
                break;
            }
        }
        if (!hasMagnitude) {
            return Collections.emptyList();
        }

        List<SemanticSearchResultDto> matches = new ArrayList<>();

        for (AgriculturalKnowledgeChunk chunk : index) {
            if (chunk.getVectorEmbedding() == null) {
                continue;
            }
            float similarity = embeddingService.cosineSimilarity(queryEmbedding, chunk.getVectorEmbedding());
            if (similarity >= minSimilarityThreshold) {
                matches.add(new SemanticSearchResultDto(chunk, similarity));
            }
        }

        matches.sort((a, b) -> Float.compare(b.getSimilarityScore(), a.getSimilarityScore()));

        return matches.stream().limit(topK).collect(Collectors.toList());
    }

    @Override
    public void indexChunk(AgriculturalKnowledgeChunk chunk) {
        if (chunk != null) {
            if (chunk.getVectorEmbedding() == null && chunk.getContent() != null) {
                chunk.setVectorEmbedding(embeddingService.generateEmbedding(chunk.getTitle() + " " + chunk.getContent()));
            }
            index.add(chunk);
        }
    }

    @Override
    public int getIndexedChunkCount() {
        return index.size();
    }

    @Override
    public void clearIndex() {
        index.clear();
    }

    @Override
    public List<AgriculturalKnowledgeChunk> getAllChunks() {
        return Collections.unmodifiableList(index);
    }
}
