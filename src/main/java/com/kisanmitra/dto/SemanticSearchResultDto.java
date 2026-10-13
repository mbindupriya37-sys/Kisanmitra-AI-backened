package com.kisanmitra.dto;

/**
 * Result DTO returned by the in-memory vector search engine,
 * pairing a retrieved knowledge chunk with its cosine similarity score.
 */
public class SemanticSearchResultDto {

    private final AgriculturalKnowledgeChunk chunk;
    private final float similarityScore;

    public SemanticSearchResultDto(AgriculturalKnowledgeChunk chunk, float similarityScore) {
        this.chunk = chunk;
        this.similarityScore = similarityScore;
    }

    public AgriculturalKnowledgeChunk getChunk() {
        return chunk;
    }

    public float getSimilarityScore() {
        return similarityScore;
    }
}
