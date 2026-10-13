package com.kisanmitra.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Spring Cache Configuration using Caffeine in-memory caching.
 * Provides bounded cache sizes (500 items) and time-to-live (60 minutes)
 * for stable reference data (Government Schemes, Crop Disease Knowledge).
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_GOVERNMENT_SCHEMES = "governmentSchemes";
    public static final String CACHE_GOVERNMENT_SCHEME_BY_ID = "governmentSchemeById";
    public static final String CACHE_CROP_DISEASE_KNOWLEDGE = "cropDiseaseKnowledge";
    public static final String CACHE_CROP_DISEASE_KNOWLEDGE_BY_ID = "cropDiseaseKnowledgeById";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .recordStats());
        cacheManager.setCacheNames(Arrays.asList(
                CACHE_GOVERNMENT_SCHEMES,
                CACHE_GOVERNMENT_SCHEME_BY_ID,
                CACHE_CROP_DISEASE_KNOWLEDGE,
                CACHE_CROP_DISEASE_KNOWLEDGE_BY_ID
        ));
        return cacheManager;
    }
}
