package com.kisanmitra.dto;

import com.kisanmitra.entity.AiChatMessage;
import java.time.LocalDateTime;

public class AiChatMessageDto {

    private Long id;
    private Long sessionId;
    private String sender;
    private String content;
    private String groundedSources;
    private Boolean isEscalatedToKvk;
    private LocalDateTime timestamp;

    public AiChatMessageDto() {
    }

    public AiChatMessageDto(AiChatMessage message) {
        if (message != null) {
            this.id = message.getId();
            this.sessionId = message.getSession() != null ? message.getSession().getId() : null;
            this.sender = message.getSender();
            this.content = message.getContent();
            this.groundedSources = message.getGroundedSources();
            this.isEscalatedToKvk = message.getIsEscalatedToKvk();
            this.timestamp = message.getTimestamp();
        }
    }

    public AiChatMessageDto(Long id, Long sessionId, String sender, String content, String groundedSources, Boolean isEscalatedToKvk, LocalDateTime timestamp) {
        this.id = id;
        this.sessionId = sessionId;
        this.sender = sender;
        this.content = content;
        this.groundedSources = groundedSources;
        this.isEscalatedToKvk = isEscalatedToKvk;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroundedSources() {
        return groundedSources;
    }

    public void setGroundedSources(String groundedSources) {
        this.groundedSources = groundedSources;
    }

    public Boolean getIsEscalatedToKvk() {
        return isEscalatedToKvk;
    }

    public void setIsEscalatedToKvk(Boolean isEscalatedToKvk) {
        this.isEscalatedToKvk = isEscalatedToKvk;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
