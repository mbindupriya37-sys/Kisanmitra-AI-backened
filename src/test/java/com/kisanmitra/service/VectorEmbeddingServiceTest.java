package com.kisanmitra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VectorEmbeddingServiceTest {

    private VectorEmbeddingService embeddingService;

    @BeforeEach
    void setUp() {
        embeddingService = new VectorEmbeddingService();
    }

    @Test
    @DisplayName("Embedding vector must have exactly 128 dimensions")
    void testEmbeddingDimensions() {
        float[] vector = embeddingService.generateEmbedding("Rice blast disease symptoms and organic fungicide management");
        assertNotNull(vector);
        assertEquals(VectorEmbeddingService.VECTOR_DIMENSION, vector.length);
        assertEquals(128, vector.length);
    }

    @Test
    @DisplayName("Generated embedding vector for non-empty text must be L2 normalized (magnitude = 1.0)")
    void testL2Normalization() {
        float[] vector = embeddingService.generateEmbedding("Wheat rust fungal spore infection control");
        double sumSq = 0.0;
        for (float val : vector) {
            sumSq += (double) val * (double) val;
        }
        double magnitude = Math.sqrt(sumSq);
        assertEquals(1.0, magnitude, 1e-4, "L2 magnitude of normalized vector should be approximately 1.0");
    }

    @Test
    @DisplayName("Empty, null, whitespace, or stop-word-only text must return zero-vector with magnitude 0")
    void testEmptyAndNullInputsReturnZeroVector() {
        float[] nullVec = embeddingService.generateEmbedding(null);
        float[] emptyVec = embeddingService.generateEmbedding("");
        float[] blankVec = embeddingService.generateEmbedding("    \t\n  ");
        float[] punctVec = embeddingService.generateEmbedding("!@#$%^&*()_+{}[]:;\"'<>?,./");
        float[] stopVec = embeddingService.generateEmbedding("the is at which on a an and or");

        assertAll(
                () -> assertEquals(128, nullVec.length),
                () -> assertEquals(0.0f, computeMagnitude(nullVec), 1e-6),
                () -> assertEquals(0.0f, computeMagnitude(emptyVec), 1e-6),
                () -> assertEquals(0.0f, computeMagnitude(blankVec), 1e-6),
                () -> assertEquals(0.0f, computeMagnitude(punctVec), 1e-6),
                () -> assertEquals(0.0f, computeMagnitude(stopVec), 1e-6)
        );
    }

    @Test
    @DisplayName("Cosine similarity between identical non-zero vectors must equal 1.0")
    void testCosineSimilarityIdentity() {
        float[] vec = embeddingService.generateEmbedding("Cotton bollworm pest management in Andhra Pradesh");
        float similarity = embeddingService.cosineSimilarity(vec, vec);
        assertEquals(1.0f, similarity, 1e-4);
    }

    @Test
    @DisplayName("Cosine similarity with zero-vector must return 0.0 without throwing exceptions")
    void testCosineSimilarityZeroVector() {
        float[] normalVec = embeddingService.generateEmbedding("Maize nitrogen fertilizer dosage");
        float[] zeroVec = new float[128];

        assertEquals(0.0f, embeddingService.cosineSimilarity(normalVec, zeroVec), 1e-6);
        assertEquals(0.0f, embeddingService.cosineSimilarity(zeroVec, normalVec), 1e-6);
        assertEquals(0.0f, embeddingService.cosineSimilarity(zeroVec, zeroVec), 1e-6);
    }

    @Test
    @DisplayName("Cosine similarity must remain strictly bounded within [-1.0, 1.0]")
    void testCosineSimilarityBounds() {
        float[] vec1 = embeddingService.generateEmbedding("Bacterial blight in paddy fields");
        float[] vec2 = embeddingService.generateEmbedding("PM-Kisan direct benefit transfer installment");

        float similarity = embeddingService.cosineSimilarity(vec1, vec2);
        assertTrue(similarity >= -1.0f && similarity <= 1.0f, "Similarity must be bounded in [-1.0, 1.0]");
    }

    @Test
    @DisplayName("Null or mismatched vector dimensions must return 0.0f safely")
    void testMismatchedOrNullVectorsReturnZero() {
        float[] validVec = embeddingService.generateEmbedding("Rice");
        float[] shortVec = new float[64];

        assertEquals(0.0f, embeddingService.cosineSimilarity(null, validVec));
        assertEquals(0.0f, embeddingService.cosineSimilarity(validVec, null));
        assertEquals(0.0f, embeddingService.cosineSimilarity(shortVec, validVec));
    }

    @Test
    @DisplayName("Semantically related agricultural terms must exhibit higher cosine similarity than unrelated terms")
    void testSemanticRelevance() {
        float[] queryVec = embeddingService.generateEmbedding("paddy blast fungal disease lesions");
        float[] relatedDoc = embeddingService.generateEmbedding("Rice blast caused by Pyricularia oryzae fungus produces spindle-shaped lesions");
        float[] unrelatedDoc = embeddingService.generateEmbedding("Tractor machinery bank loan subsidy credit interest rate scheme");

        float simRelated = embeddingService.cosineSimilarity(queryVec, relatedDoc);
        float simUnrelated = embeddingService.cosineSimilarity(queryVec, unrelatedDoc);

        assertTrue(simRelated > 0.40f, "Related agricultural document should have substantial similarity (> 0.40), got: " + simRelated);
        assertTrue(simRelated > simUnrelated, "Related similarity (" + simRelated + ") must exceed unrelated similarity (" + simUnrelated + ")");
    }

    private double computeMagnitude(float[] vec) {
        double sum = 0.0;
        for (float v : vec) {
            sum += (double) v * (double) v;
        }
        return Math.sqrt(sum);
    }
}
