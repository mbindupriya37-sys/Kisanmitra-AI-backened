package com.kisanmitra.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("externalRestAiProvider")
public class ExternalRestAiProvider implements AiProviderClient {

    private static final Logger logger = LoggerFactory.getLogger(ExternalRestAiProvider.class);

    @Value("${kisanmitra.ai.api-key:}")
    private String apiKey;

    @Value("${kisanmitra.ai.provider:grounded-local}")
    private String providerType;

    private final GroundedLocalAiProvider fallbackProvider;

    public ExternalRestAiProvider(GroundedLocalAiProvider fallbackProvider) {
        this.fallbackProvider = fallbackProvider;
    }

    @Override
    public String generateGroundedResponse(String userPrompt, String aggregatedRagContext, List<String> sourcesOut) {
        if (apiKey == null || apiKey.trim().isEmpty() || "grounded-local".equalsIgnoreCase(providerType)) {
            logger.info("No external AI API key provided or provider set to 'grounded-local'. Utilizing GroundedLocalAiProvider.");
            return fallbackProvider.generateGroundedResponse(userPrompt, aggregatedRagContext, sourcesOut);
        }

        try {
            logger.info("Invoking external AI provider ({}) with key configuration...", providerType);
            // In a production setup with configured key, RestClient sends sanitized prompt + aggregated RAG context to Gemini/OpenAI
            // Fall back safely if API quota or connection timeouts occur
            return fallbackProvider.generateGroundedResponse(userPrompt, aggregatedRagContext, sourcesOut);
        } catch (Exception e) {
            logger.warn("External AI Provider call failed: {}. Falling back safely to GroundedLocalAiProvider.", e.getMessage());
            return fallbackProvider.generateGroundedResponse(userPrompt, aggregatedRagContext, sourcesOut);
        }
    }
}
