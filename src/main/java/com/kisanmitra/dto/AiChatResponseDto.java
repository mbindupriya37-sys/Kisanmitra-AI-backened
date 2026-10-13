package com.kisanmitra.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AiChatResponseDto {

    private Long sessionId;
    private String userMessage;
    private String assistantReply;
    private List<String> groundedSources;
    private Boolean isEscalatedToKvk;
    private String disclaimer;
    private LocalDateTime timestamp;

    public AiChatResponseDto() {
    }

    public AiChatResponseDto(Long sessionId, String userMessage, String assistantReply,
                             List<String> groundedSources, Boolean isEscalatedToKvk,
                             String disclaimer, LocalDateTime timestamp) {
        this.sessionId = sessionId;
        this.userMessage = userMessage;
        this.assistantReply = assistantReply;
        this.groundedSources = groundedSources;
        this.isEscalatedToKvk = isEscalatedToKvk;
        this.disclaimer = disclaimer;
        this.timestamp = timestamp;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getAssistantReply() {
        return assistantReply;
    }

    public void setAssistantReply(String assistantReply) {
        this.assistantReply = assistantReply;
    }

    public List<String> getGroundedSources() {
        return groundedSources;
    }

    public void setGroundedSources(List<String> groundedSources) {
        this.groundedSources = groundedSources;
    }

    public Boolean getIsEscalatedToKvk() {
        return isEscalatedToKvk;
    }

    public void setIsEscalatedToKvk(Boolean isEscalatedToKvk) {
        this.isEscalatedToKvk = isEscalatedToKvk;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
