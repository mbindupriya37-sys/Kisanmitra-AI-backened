package com.kisanmitra.service;

import com.kisanmitra.dto.AgriculturalKnowledgeChunk;
import com.kisanmitra.dto.SemanticSearchResultDto;

import java.util.List;

/**
 * Contract for in-memory agricultural semantic vector retrieval.
 * Provides top-K cosine similarity search against indexed knowledge chunks.
 */
public interface AgriculturalVectorIndexService {

    /**
     * Initializes or re-indexes agricultural knowledge chunks from repositories and baselines.
     */
    void initializeIndex();

    /**
     * Performs top-K semantic vector search against indexed knowledge chunks.
     *
     * @param queryText input user prompt or agricultural question
     * @param topK maximum number of matches to retrieve
     * @param minSimilarityThreshold minimum cosine similarity score (e.g. 0.35f)
     * @return ranked list of results, empty if none exceed threshold
     */
    List<SemanticSearchResultDto> search(String queryText, int topK, float minSimilarityThreshold);

    /**
     * Adds an agricultural knowledge chunk to the index.
     */
    void indexChunk(AgriculturalKnowledgeChunk chunk);

    /**
     * Returns total number of indexed knowledge chunks.
     */
    int getIndexedChunkCount();

    /**
     * Clears all knowledge chunks from the index.
     */
    void clearIndex();

    /**
     * Returns an unmodifiable view of all indexed knowledge chunks.
     */
    List<AgriculturalKnowledgeChunk> getAllChunks();
}
