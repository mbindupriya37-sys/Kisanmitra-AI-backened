package com.kisanmitra.dto;

import com.kisanmitra.entity.AiChatSession;
import java.time.LocalDateTime;

public class AiChatSessionDto {

    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer messageCount;

    public AiChatSessionDto() {
    }

    public AiChatSessionDto(AiChatSession session) {
        this.id = session.getId();
        this.title = session.getTitle();
        this.createdAt = session.getCreatedAt();
        this.updatedAt = session.getUpdatedAt();
        this.messageCount = session.getMessages() != null ? session.getMessages().size() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }
}
