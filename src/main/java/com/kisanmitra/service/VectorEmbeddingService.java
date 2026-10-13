package com.kisanmitra.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Lightweight, zero-dependency in-memory vector embedding service.
 * Generates fixed-dimension (128-d) L2-normalized semantic feature embeddings
 * using agricultural domain lexicon weighting and subword character n-gram hashing.
 * Computes cosine similarity bounded in [-1.0, 1.0].
 */
@Service
public class VectorEmbeddingService {

    public static final int VECTOR_DIMENSION = 128;

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by",
            "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't",
            "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have",
            "haven't", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how", "i",
            "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most",
            "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other",
            "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "should", "shouldn't",
            "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then",
            "there", "there's", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up",
            "very", "was", "wasn't", "we", "were", "weren't", "what", "what's", "when", "where", "which", "while",
            "who", "whom", "why", "with", "won't", "would", "wouldn't", "you", "your", "yours", "yourself", "yourselves"
    );

    private static final Map<String, Float> DOMAIN_WEIGHTS = createDomainWeights();

    private static Map<String, Float> createDomainWeights() {
        Map<String, Float> map = new HashMap<>();
        // Major Crops
        map.put("rice", 3.5f);
        map.put("paddy", 3.5f);
        map.put("wheat", 3.5f);
        map.put("maize", 3.5f);
        map.put("cotton", 3.5f);
        map.put("chickpea", 3.5f);
        map.put("gram", 3.5f);
        map.put("pigeonpea", 3.5f);
        map.put("arhar", 3.5f);
        map.put("tur", 3.5f);
        map.put("groundnut", 3.5f);
        map.put("peanut", 3.5f);
        map.put("sugarcane", 3.5f);
        map.put("soybean", 3.5f);
        map.put("mustard", 3.5f);

        // Disease, Pests & Pathogens
        map.put("blast", 4.0f);
        map.put("blight", 4.0f);
        map.put("rot", 4.0f);
        map.put("wilt", 4.0f);
        map.put("rust", 4.0f);
        map.put("smut", 4.0f);
        map.put("mildew", 4.0f);
        map.put("spot", 3.5f);
        map.put("canker", 4.0f);
        map.put("borer", 4.0f);
        map.put("bollworm", 4.0f);
        map.put("aphid", 4.0f);
        map.put("fungal", 3.0f);
        map.put("fungus", 3.0f);
        map.put("bacterial", 3.0f);
        map.put("bacteria", 3.0f);
        map.put("viral", 3.0f);
        map.put("virus", 3.0f);
        map.put("symptom", 3.0f);
        map.put("symptoms", 3.0f);
        map.put("lesion", 3.0f);
        map.put("lesions", 3.0f);
        map.put("pesticide", 3.0f);
        map.put("fungicide", 3.0f);
        map.put("neem", 3.0f);
        map.put("pathogen", 3.0f);
        map.put("kvk", 3.5f);
        map.put("icar", 3.5f);
        map.put("pyricularia", 4.5f);
        map.put("xanthomonas", 4.5f);
        map.put("fusarium", 4.5f);

        // Agronomic, Soil & Climate Parameters
        map.put("soil", 3.0f);
        map.put("ph", 3.5f);
        map.put("nitrogen", 3.0f);
        map.put("phosphorus", 3.0f);
        map.put("potassium", 3.0f);
        map.put("npk", 3.5f);
        map.put("rainfall", 3.0f);
        map.put("rain", 3.0f);
        map.put("temperature", 3.0f);
        map.put("water", 3.0f);
        map.put("irrigation", 3.0f);
        map.put("moisture", 3.0f);
        map.put("humidity", 3.0f);
        map.put("monsoon", 3.0f);
        map.put("clay", 2.5f);
        map.put("loam", 2.5f);
        map.put("sandy", 2.5f);
        map.put("black", 2.5f);
        map.put("alluvial", 2.5f);

        // Market, Mandi & Government Schemes
        map.put("scheme", 3.5f);
        map.put("schemes", 3.5f);
        map.put("subsidy", 3.5f);
        map.put("subsidies", 3.5f);
        map.put("pmkisan", 4.5f);
        map.put("kisan", 3.0f);
        map.put("mandi", 3.5f);
        map.put("agmarknet", 4.0f);
        map.put("price", 3.0f);
        map.put("prices", 3.0f);
        map.put("modal", 3.0f);
        map.put("msp", 3.5f);
        map.put("arrival", 3.0f);
        map.put("arrivals", 3.0f);
        map.put("insurance", 3.5f);
        map.put("pmfby", 4.5f);
        map.put("kcc", 4.0f);
        map.put("credit", 3.0f);

        return Collections.unmodifiableMap(map);
    }

    /**
     * Generates a fixed 128-dimensional L2-normalized float vector for given text.
     * Returns a zero vector for null, empty, or whitespace-only inputs.
     */
    public float[] generateEmbedding(String text) {
        float[] vector = new float[VECTOR_DIMENSION];
        if (text == null || text.trim().isEmpty()) {
            return vector;
        }

        String sanitized = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (sanitized.isEmpty()) {
            return vector;
        }

        String[] tokens = sanitized.split(" ");
        boolean hasContent = false;

        for (String token : tokens) {
            if (token.isEmpty() || STOP_WORDS.contains(token)) {
                continue;
            }

            hasContent = true;
            float weight = DOMAIN_WEIGHTS.getOrDefault(token, 1.0f);

            // Project primary token into embedding dimension
            int primaryHash = hashToken(token, 0);
            int primaryIndex = Math.abs(primaryHash) % VECTOR_DIMENSION;
            float primarySign = (hashToken(token, 1) & 1) == 0 ? 1.0f : -1.0f;
            vector[primaryIndex] += primarySign * weight;

            // Generate character 3-grams for subword morphological matching
            if (token.length() >= 4) {
                float subwordWeight = weight * 0.45f;
                for (int i = 0; i <= token.length() - 3; i++) {
                    String trigram = token.substring(i, i + 3);
                    int triHash = hashToken(trigram, 2);
                    int triIndex = Math.abs(triHash) % VECTOR_DIMENSION;
                    float triSign = (hashToken(trigram, 3) & 1) == 0 ? 1.0f : -1.0f;
                    vector[triIndex] += triSign * subwordWeight;
                }
            }
        }

        if (!hasContent) {
            return new float[VECTOR_DIMENSION];
        }

        // L2 Normalization
        l2Normalize(vector);
        return vector;
    }

    /**
     * Computes cosine similarity between two float vectors.
     * Returns 0.0f if either vector is null, lengths mismatch, or magnitude is zero.
     * Output is mathematically bounded in [-1.0f, 1.0f].
     */
    public float cosineSimilarity(float[] vectorA, float[] vectorB) {
        if (vectorA == null || vectorB == null) {
            return 0.0f;
        }
        if (vectorA.length != VECTOR_DIMENSION || vectorB.length != VECTOR_DIMENSION) {
            return 0.0f;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < VECTOR_DIMENSION; i++) {
            dotProduct += (double) vectorA[i] * (double) vectorB[i];
            normA += (double) vectorA[i] * (double) vectorA[i];
            normB += (double) vectorB[i] * (double) vectorB[i];
        }

        if (normA <= 1e-12 || normB <= 1e-12) {
            return 0.0f;
        }

        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        // Bound cleanly between -1.0 and 1.0 to guard against floating-point rounding
        if (similarity > 1.0) similarity = 1.0;
        if (similarity < -1.0) similarity = -1.0;

        return (float) similarity;
    }

    private void l2Normalize(float[] vector) {
        double sumSq = 0.0;
        for (float v : vector) {
            sumSq += (double) v * (double) v;
        }

        if (sumSq > 1e-12) {
            float magnitude = (float) Math.sqrt(sumSq);
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= magnitude;
            }
        }
    }

    /**
     * Deterministic polynomial hash with salt for dimension and sign projection.
     */
    private int hashToken(String token, int salt) {
        int h = 0x811c9dc5 + salt * 31;
        for (int i = 0; i < token.length(); i++) {
            h ^= token.charAt(i);
            h *= 0x01000193;
        }
        return h;
    }
}
