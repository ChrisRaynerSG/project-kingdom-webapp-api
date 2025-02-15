package com.carnasa.cr.projectkingdomwebpage.models.devlog;

import java.util.UUID;

public class DevLogPostReplyPostDto {
    String message;
    UUID userId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
