package com.kisanmitra.service;

import java.util.List;

public interface AiProviderClient {

    String generateGroundedResponse(String userPrompt, String aggregatedRagContext, List<String> sourcesOut);
}
