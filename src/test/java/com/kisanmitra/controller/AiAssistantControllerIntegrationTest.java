package com.kisanmitra.controller;

import com.kisanmitra.entity.AiChatMessage;
import com.kisanmitra.entity.AiChatSession;
import com.kisanmitra.repository.AiChatMessageRepository;
import com.kisanmitra.repository.AiChatSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AiAssistantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AiChatSessionRepository sessionRepository;

    @Autowired
    private AiChatMessageRepository messageRepository;

    @Test
    @WithMockUser(username = "farmer.rag@kisanmitra.ai", roles = {"FARMER"})
    @DisplayName("GET /api/ai-assistant/sessions/{id} returns AiChatMessageDto list with DTO contracts and no entity leak")
    void testGetSessionMessagesReturnsDtoList() throws Exception {
        String farmerEmail = "farmer.rag@kisanmitra.ai";
        AiChatSession session = new AiChatSession(farmerEmail, "Soil Health Discussion");
        session = sessionRepository.saveAndFlush(session);

        AiChatMessage userMsg = new AiChatMessage(session, "USER", "What is the recommended soil pH for Wheat?", null, false);
        messageRepository.saveAndFlush(userMsg);

        AiChatMessage assistantMsg = new AiChatMessage(session, "ASSISTANT", "Wheat grows best in soil pH between 6.0 and 7.5.", "ICAR Agronomy Handbook", false);
        messageRepository.saveAndFlush(assistantMsg);

        mockMvc.perform(get("/api/ai-assistant/sessions/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].sessionId").value(session.getId()))
                .andExpect(jsonPath("$[0].sender").value("USER"))
                .andExpect(jsonPath("$[0].content").value("What is the recommended soil pH for Wheat?"))
                .andExpect(jsonPath("$[0].timestamp").exists())
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$[1].sessionId").value(session.getId()))
                .andExpect(jsonPath("$[1].sender").value("ASSISTANT"))
                .andExpect(jsonPath("$[1].content").value("Wheat grows best in soil pH between 6.0 and 7.5."))
                .andExpect(jsonPath("$[1].groundedSources").value("ICAR Agronomy Handbook"))
                .andExpect(jsonPath("$[1].isEscalatedToKvk").value(false))
                .andExpect(jsonPath("$[1].timestamp").exists())
                // Verify no raw entity fields like session object are leaked in DTO
                .andExpect(jsonPath("$[0].session.userEmail").doesNotExist());
    }

    @Test
    @WithMockUser(username = "other.farmer@kisanmitra.ai", roles = {"FARMER"})
    @DisplayName("GET /api/ai-assistant/sessions/{id} rejects unauthorized session query with 403 Forbidden")
    void testGetSessionMessagesRejectsOtherUserAccess() throws Exception {
        String ownerEmail = "owner.farmer@kisanmitra.ai";
        AiChatSession session = new AiChatSession(ownerEmail, "Private Rice Discussion");
        session = sessionRepository.saveAndFlush(session);

        mockMvc.perform(get("/api/ai-assistant/sessions/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/ai-assistant/sessions/{id} rejects unauthenticated requests")
    void testGetSessionMessagesRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/ai-assistant/sessions/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
