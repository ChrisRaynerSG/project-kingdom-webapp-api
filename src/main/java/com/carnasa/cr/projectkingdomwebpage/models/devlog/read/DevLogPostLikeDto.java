package com.carnasa.cr.projectkingdomwebpage.models.devlog.read;

import java.time.LocalDateTime;
public class DevLogPostLikeDto {
    String username;
    LocalDateTime createdAt;

    public DevLogPostLikeDto(String username, LocalDateTime createdAt) {
        this.username = username;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
