package com.kisanmitra.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_chat_messages")
public class AiChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private AiChatSession session;

    @Column(nullable = false)
    private String sender; // USER or ASSISTANT

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String groundedSources; // JSON or comma-separated citations

    private Boolean isEscalatedToKvk = false;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public AiChatMessage() {
    }

    public AiChatMessage(AiChatSession session, String sender, String content, String groundedSources, Boolean isEscalatedToKvk) {
        this.session = session;
        this.sender = sender;
        this.content = content;
        this.groundedSources = groundedSources;
        this.isEscalatedToKvk = isEscalatedToKvk != null ? isEscalatedToKvk : false;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AiChatSession getSession() {
        return session;
    }

    public void setSession(AiChatSession session) {
        this.session = session;
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
