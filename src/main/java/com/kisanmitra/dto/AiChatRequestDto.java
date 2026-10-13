package com.kisanmitra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AiChatRequestDto {

    private Long sessionId;

    @NotBlank(message = "Question / message prompt must not be empty.")
    @Size(max = 500, message = "Message prompt must not exceed 500 characters.")
    private String message;

    public AiChatRequestDto() {
    }

    public AiChatRequestDto(Long sessionId, String message) {
        this.sessionId = sessionId;
        this.message = message;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
