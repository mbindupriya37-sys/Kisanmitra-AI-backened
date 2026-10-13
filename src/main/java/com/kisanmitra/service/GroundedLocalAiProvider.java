package com.kisanmitra.service;

import java.util.List;

import org.springframework.stereotype.Component;

@Component("groundedLocalAiProvider")
public class GroundedLocalAiProvider implements AiProviderClient {

    @Override
    public String generateGroundedResponse(String userPrompt, String aggregatedRagContext, List<String> sourcesOut) {
        StringBuilder response = new StringBuilder();
        String lowerPrompt = userPrompt.toLowerCase();

        response.append("### KisanMitra AI Response Structure\n\n");
        response.append("- **Official source material:** verified repository records, published source metadata, and direct official portal links.\n");
        response.append("- **Application calculations:** internal logic such as crop scoring or availability checks.\n");
        response.append("- **Model-generated suggestions:** advisory language created by the assistant and not a government quotation.\n");
        response.append("- **Unavailable information:** data not retrieved or not yet verified; no fabricated values are inserted.\n\n");

        if (aggregatedRagContext == null || aggregatedRagContext.trim().isEmpty()) {
            response.append("I checked official KisanMitra agricultural repositories and did not find a verified record-level match for this query.\n");
            response.append("> *\"").append(userPrompt).append("\"*\n\n");
            response.append("⚠️ **Data Grounding Status:** No verified, record-level entry was found in the active repositories for this specific query.\n");
            response.append("This is not an official quotation or confirmed government recommendation.\n");
            response.append("KisanMitra AI strictly refrains from inventing unverified market prices, weather forecasts, pesticide dosages, or diagnostic claims.\n\n");
            response.append("**Recommended Next Steps:**\n");
            response.append("- Re-query using standard Indian crop names (e.g., Rice, Wheat, Cotton, Maize, Chickpea, Groundnut).\n");
            response.append("- Consult your nearest **Krishi Vigyan Kendra (KVK)** or District Agricultural Officer for field inspection.\n");
            if (sourcesOut != null) {
                sourcesOut.add("ICAR - Krishi Vigyan Kendra Advisory Portal");
            }
            return response.toString();
        }

        response.append("**Evidence-backed guidance:**\n\n");
        response.append(aggregatedRagContext).append("\n\n");

        if (lowerPrompt.contains("disease") || lowerPrompt.contains("symptom") || lowerPrompt.contains("rot") || lowerPrompt.contains("blast") || lowerPrompt.contains("wilt")) {
            response.append("🌱 **Model-generated diagnostic note:**\n");
            response.append("The guidance above is based on database records and source metadata, but it is not a confirmed laboratory diagnosis. Always consult local KVK plant pathologists before applying chemical inputs.\n");
        } else if (lowerPrompt.contains("price") || lowerPrompt.contains("mandi") || lowerPrompt.contains("market")) {
            response.append("📊 **Model-generated market note:**\n");
            response.append("This market note is a decision-support summary only. Any official price observations must come from the retrieved AGMARKNET source record and are not guaranteed for all markets or dates.\n");
        } else if (lowerPrompt.contains("scheme") || lowerPrompt.contains("subsidy") || lowerPrompt.contains("pm-kisan")) {
            response.append("🏛️ **Model-generated scheme note:**\n");
            response.append("Eligibility, benefit amounts, and time-sensitive conditions vary by scheme and state. Verify the latest rules on the official government portal before submitting an application.\n");
        }

        return response.toString();
    }
}
