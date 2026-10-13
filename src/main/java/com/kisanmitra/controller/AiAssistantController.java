package com.kisanmitra.controller;

import com.kisanmitra.dto.AiChatMessageDto;
import com.kisanmitra.dto.AiChatRequestDto;
import com.kisanmitra.dto.AiChatResponseDto;
import com.kisanmitra.dto.AiChatSessionDto;
import com.kisanmitra.service.AiAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-assistant")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<AiChatResponseDto> processChat(
            @Valid @RequestBody AiChatRequestDto request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        AiChatResponseDto response = aiAssistantService.processChatQuery(request, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<List<AiChatSessionDto>> getUserSessions(Authentication authentication) {
        String userEmail = authentication.getName();
        List<AiChatSessionDto> sessions = aiAssistantService.getUserSessions(userEmail);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<List<AiChatMessageDto>> getSessionMessages(
            @PathVariable Long sessionId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<AiChatMessageDto> messages = aiAssistantService.getSessionMessages(sessionId, userEmail);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        aiAssistantService.deleteSession(sessionId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
