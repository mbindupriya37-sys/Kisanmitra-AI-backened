package com.kisanmitra.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a discrete, attributed unit of agricultural domain knowledge
 * indexed in the in-memory semantic vector store.
 */
public class AgriculturalKnowledgeChunk {

    private String id;
    private String category;
    private String title;
    private String content;
    private String sourceName;
    private String sourceUrl;
    private float[] vectorEmbedding;
    private Map<String, String> metadata = new HashMap<>();

    public AgriculturalKnowledgeChunk() {
    }

    public AgriculturalKnowledgeChunk(String id, String category, String title, String content,
                                      String sourceName, String sourceUrl, float[] vectorEmbedding) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.content = content;
        this.sourceName = sourceName;
        this.sourceUrl = sourceUrl;
        this.vectorEmbedding = vectorEmbedding;
    }

    public AgriculturalKnowledgeChunk(String id, String category, String title, String content,
                                      String sourceName, String sourceUrl, float[] vectorEmbedding,
                                      Map<String, String> metadata) {
        this(id, category, title, content, sourceName, sourceUrl, vectorEmbedding);
        if (metadata != null) {
            this.metadata.putAll(metadata);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public float[] getVectorEmbedding() {
        return vectorEmbedding;
    }

    public void setVectorEmbedding(float[] vectorEmbedding) {
        this.vectorEmbedding = vectorEmbedding;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
