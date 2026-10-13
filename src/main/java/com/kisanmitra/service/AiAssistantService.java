package com.kisanmitra.service;

import com.kisanmitra.dto.AgriculturalKnowledgeChunk;
import com.kisanmitra.dto.AiChatMessageDto;
import com.kisanmitra.dto.AiChatRequestDto;
import com.kisanmitra.dto.AiChatResponseDto;
import com.kisanmitra.dto.AiChatSessionDto;
import com.kisanmitra.dto.SemanticSearchResultDto;
import com.kisanmitra.entity.*;
import com.kisanmitra.exception.ResourceNotFoundException;
import com.kisanmitra.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiAssistantService {

    private static final Logger logger = LoggerFactory.getLogger(AiAssistantService.class);

    private static final String DEFAULT_DISCLAIMER =
            "DISCLAIMER: KisanMitra AI provides evidence-backed guidance grounded in official ICAR, AGMARKNET, Open-Meteo, and Government Scheme portals. " +
            "It is not a substitute for physical laboratory diagnosis or formal agricultural extension officer advisories.";

    private final AiChatSessionRepository sessionRepository;
    private final AiChatMessageRepository messageRepository;
    private final CropDiseaseKnowledgeRepository diseaseRepository;
    private final GovernmentSchemeRepository schemeRepository;
    private final CropRequirementRepository cropRequirementRepository;
    private final AiProviderClient aiProviderClient;
    private final AgriculturalVectorIndexService vectorIndexService;

    @Autowired
    public AiAssistantService(AiChatSessionRepository sessionRepository,
                               AiChatMessageRepository messageRepository,
                               CropDiseaseKnowledgeRepository diseaseRepository,
                               GovernmentSchemeRepository schemeRepository,
                               CropRequirementRepository cropRequirementRepository,
                               @Qualifier("externalRestAiProvider") AiProviderClient aiProviderClient,
                               @Autowired(required = false) AgriculturalVectorIndexService vectorIndexService) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.diseaseRepository = diseaseRepository;
        this.schemeRepository = schemeRepository;
        this.cropRequirementRepository = cropRequirementRepository;
        this.aiProviderClient = aiProviderClient;
        this.vectorIndexService = vectorIndexService;
    }

    public AiAssistantService(AiChatSessionRepository sessionRepository,
                               AiChatMessageRepository messageRepository,
                               CropDiseaseKnowledgeRepository diseaseRepository,
                               GovernmentSchemeRepository schemeRepository,
                               CropRequirementRepository cropRequirementRepository,
                               AiProviderClient aiProviderClient) {
        this(sessionRepository, messageRepository, diseaseRepository, schemeRepository,
                cropRequirementRepository, aiProviderClient, null);
    }

    @Transactional
    public AiChatResponseDto processChatQuery(AiChatRequestDto request, String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new AccessDeniedException("Authenticated user identity is required.");
        }

        String userPrompt = request.getMessage() != null ? request.getMessage().trim() : "";
        if (userPrompt.isEmpty()) {
            throw new IllegalArgumentException("Question / prompt must not be empty.");
        }
        if (userPrompt.length() > 500) {
            throw new IllegalArgumentException("Message prompt exceeds maximum length of 500 characters.");
        }

        // 1. Safety & Prompt Defense Check
        if (containsPromptInjection(userPrompt)) {
            logger.warn("Prompt injection attempt detected from user {}: {}", userEmail, userPrompt);
            return createRefusalResponse(request.getSessionId(), userPrompt,
                    "⚠️ **Safety Advisory**: Your query contains override instructions that violate KisanMitra AI safety policies. Please ask a direct agricultural question about crops, weather, market prices, government schemes, or plant diseases.");
        }

        // 2. Obtain or Create User-Isolated Session
        AiChatSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + request.getSessionId()));
            if (!session.getUserEmail().equalsIgnoreCase(userEmail.trim())) {
                throw new AccessDeniedException("Access denied: You are not authorized to view or post to this chat session.");
            }
        } else {
            String sessionTitle = userPrompt.length() > 30 ? userPrompt.substring(0, 27) + "..." : userPrompt;
            session = sessionRepository.save(new AiChatSession(userEmail.trim(), sessionTitle));
        }

        // Save User Message
        AiChatMessage userMsg = new AiChatMessage(session, "USER", userPrompt, null, false);
        messageRepository.save(userMsg);

        // 3. Retrieval-Augmented Grounding (RAG Context Aggregation)
        List<String> sources = new ArrayList<>();
        String ragContext = aggregateRagContext(userPrompt, sources);

        // 4. Delegate to AI Provider
        String assistantReply = aiProviderClient.generateGroundedResponse(userPrompt, ragContext, sources);
        boolean isEscalated = assistantReply.contains("Urgent KVK") || assistantReply.contains("laboratory testing");

        String sourcesJson = String.join(", ", sources);
        AiChatMessage assistantMsg = new AiChatMessage(session, "ASSISTANT", assistantReply, sourcesJson, isEscalated);
        messageRepository.save(assistantMsg);

        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        return new AiChatResponseDto(
                session.getId(),
                userPrompt,
                assistantReply,
                sources,
                isEscalated,
                DEFAULT_DISCLAIMER,
                LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public List<AiChatSessionDto> getUserSessions(String userEmail) {
        return sessionRepository.findByUserEmailOrderByUpdatedAtDesc(userEmail.trim())
                .stream()
                .map(AiChatSessionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AiChatMessageDto> getSessionMessages(Long sessionId, String userEmail) {
        AiChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + sessionId));
        if (!session.getUserEmail().equalsIgnoreCase(userEmail.trim())) {
            throw new AccessDeniedException("Access denied: You are not authorized to view messages in this session.");
        }
        return messageRepository.findBySessionIdOrderByTimestampAsc(sessionId)
                .stream()
                .map(AiChatMessageDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSession(Long sessionId, String userEmail) {
        AiChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found with id: " + sessionId));
        if (!session.getUserEmail().equalsIgnoreCase(userEmail.trim())) {
            throw new AccessDeniedException("Access denied: You cannot delete another user's session.");
        }
        sessionRepository.delete(session);
    }

    private boolean containsPromptInjection(String input) {
        String lower = input.toLowerCase();
        return lower.contains("ignore previous instructions")
                || lower.contains("system prompt:")
                || lower.contains("forget all instructions")
                || lower.contains("override rules")
                || lower.contains("you are now a jailbroken");
    }

    private String aggregateRagContext(String prompt, List<String> sourcesOut) {
        StringBuilder ragBuilder = new StringBuilder();
        String lower = prompt.toLowerCase();

        // 1. Vector-Style Semantic Retrieval (Phase 10B)
        if (vectorIndexService != null) {
            List<SemanticSearchResultDto> semanticMatches = vectorIndexService.search(prompt, 3, 0.35f);
            if (!semanticMatches.isEmpty()) {
                ragBuilder.append("🧠 **Retrieved Semantic Agricultural Knowledge (Vector Search):**\n");
                for (SemanticSearchResultDto match : semanticMatches) {
                    AgriculturalKnowledgeChunk chunk = match.getChunk();
                    ragBuilder.append(chunk.getContent()).append("\n");
                    if (chunk.getSourceName() != null && !chunk.getSourceName().isEmpty()) {
                        String sourceEntry = chunk.getSourceName() + (chunk.getSourceUrl() != null && !chunk.getSourceUrl().isEmpty() ? " (" + chunk.getSourceUrl() + ")" : "");
                        if (!sourcesOut.contains(sourceEntry)) {
                            sourcesOut.add(sourceEntry);
                        }
                    }
                }
            }
        }

        // 2. Keyword & Entity-Based Domain Grounding (Fallback / Complementary)
        // Check Diseases Repository
        List<CropDiseaseKnowledge> diseases = diseaseRepository.findAll();
        for (CropDiseaseKnowledge d : diseases) {
            if ("VERIFIED".equalsIgnoreCase(d.getVerificationStatus()) &&
                    (lower.contains(d.getCropName().toLowerCase()) || lower.contains(d.getDiseaseName().toLowerCase()) || isKeywordInPrompt(lower, d.getSymptomsDescription()))) {
                if (!ragBuilder.toString().contains(d.getDiseaseName())) {
                    ragBuilder.append("🦠 **Disease Record [").append(d.getCropName()).append(" - ").append(d.getDiseaseName()).append("]:**\n");
                    ragBuilder.append("- Scientific Name: *").append(d.getScientificName()).append("*\n");
                    ragBuilder.append("- Symptoms: ").append(d.getSymptomsDescription()).append("\n");
                    ragBuilder.append("- Non-Chemical Control: ").append(d.getNonChemicalManagement()).append("\n");
                    ragBuilder.append("- Cultural Practices: ").append(d.getCulturalPractices()).append("\n");
                }
                String sourceEntry = d.getSourceName() + " (" + d.getSourceUrl() + ")";
                if (!sourcesOut.contains(sourceEntry)) {
                    sourcesOut.add(sourceEntry);
                }
                break;
            }
        }

        // Check Mandi / Market Prices
        if (lower.contains("price") || lower.contains("mandi") || lower.contains("market") || lower.contains("rate")) {
            if (!ragBuilder.toString().contains("Market Data Status")) {
                ragBuilder.append("📊 **Market Data Status:**\n");
                ragBuilder.append("- No live AGMARKNET market observation was retrieved for this query at the time of request.\n");
                ragBuilder.append("- KisanMitra does not fabricate mandi prices, arrivals, or trends without a verified source.\n");
            }
            String mandiSrc = "AGMARKNET - Directorate of Marketing & Inspection (https://agmarknet.gov.in)";
            if (!sourcesOut.contains(mandiSrc)) {
                sourcesOut.add(mandiSrc);
            }
        }

        // Check Government Schemes
        List<GovernmentScheme> schemes = schemeRepository.findAll();
        for (GovernmentScheme s : schemes) {
            if (lower.contains(s.getName().toLowerCase()) || lower.contains("scheme") || lower.contains("subsidy") || lower.contains("pm-kisan") || lower.contains("insurance")) {
                if ("VERIFIED".equalsIgnoreCase(s.getVerificationStatus())) {
                    if (!ragBuilder.toString().contains(s.getName())) {
                        ragBuilder.append("🏛️ **Government Scheme [").append(s.getName()).append("]:**\n");
                        ragBuilder.append("- Purpose: ").append(s.getPurpose()).append("\n");
                        ragBuilder.append("- Eligible Applicants: ").append(s.getEligibleApplicants()).append("\n");
                        ragBuilder.append("- Key Benefits: ").append(s.getBenefits()).append("\n");
                        ragBuilder.append("- Application Portal: ").append(s.getOfficialPortalUrl()).append("\n");
                    }
                    String schemeSrc = s.getSourceName() + " (" + s.getOfficialPortalUrl() + ")";
                    if (!sourcesOut.contains(schemeSrc)) {
                        sourcesOut.add(schemeSrc);
                    }
                    break;
                }
            }
        }

        // Check Crop Requirements
        List<CropRequirement> requirements = cropRequirementRepository.findAll();
        for (CropRequirement req : requirements) {
            if (req.getCrop() != null && lower.contains(req.getCrop().getName().toLowerCase())) {
                if (!ragBuilder.toString().contains(req.getCrop().getName())) {
                    ragBuilder.append("🌱 **Agronomic Requirements [").append(req.getCrop().getName()).append("]:**\n");
                    ragBuilder.append("- Temperature: ").append(req.getMinTemperatureRange()).append("°C - ").append(req.getMaxTemperatureRange()).append("°C\n");
                    ragBuilder.append("- Ideal Soil pH: ").append(req.getMinSoilPh()).append(" - ").append(req.getMaxSoilPh()).append("\n");
                    ragBuilder.append("- Rainfall Range: ").append(req.getMinRainfall()).append("mm - ").append(req.getMaxRainfall()).append("mm\n");
                    ragBuilder.append("- Water Description: ").append(req.getWaterRequirementDescription()).append("\n");
                }
                String reqSrc = req.getSourceName() + " (" + req.getSourceUrl() + ")";
                if (!sourcesOut.contains(reqSrc)) {
                    sourcesOut.add(reqSrc);
                }
                break;
            }
        }

        // Check Weather Context
        if (lower.contains("weather") || lower.contains("rain") || lower.contains("temperature") || lower.contains("climate")) {
            if (!ragBuilder.toString().contains("Weather Data Status")) {
                ragBuilder.append("🌤️ **Weather Data Status:**\n");
                ragBuilder.append("- No live weather forecast was retrieved for this query at the time of request.\n");
                ragBuilder.append("- The application will not substitute unverified forecast values or advisories for missing meteorological data.\n");
            }
            String weatherSrc = "Open-Meteo Weather API (https://open-meteo.com)";
            if (!sourcesOut.contains(weatherSrc)) {
                sourcesOut.add(weatherSrc);
            }
        }

        return ragBuilder.toString();
    }

    private boolean isKeywordInPrompt(String lowerPrompt, String text) {
        if (text == null) return false;
        String[] words = text.toLowerCase().split("\\s+");
        for (String w : words) {
            if (w.length() > 4 && lowerPrompt.contains(w)) {
                return true;
            }
        }
        return false;
    }

    private AiChatResponseDto createRefusalResponse(Long sessionId, String userPrompt, String refusalMsg) {
        return new AiChatResponseDto(
                sessionId,
                userPrompt,
                refusalMsg,
                List.of("KisanMitra AI Safety Policy"),
                false,
                DEFAULT_DISCLAIMER,
                LocalDateTime.now()
        );
    }
}
