package com.carnasa.cr.projectkingdomwebpage.models.devlog;

import java.time.LocalDateTime;

public class DevLogPostReplyDto {
    private Long id;
    private String message;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private Boolean isUpdated;
    private Integer likes;

    public DevLogPostReplyDto(
            Long id,
            String message,
            String author,
            LocalDateTime createdAt,
            LocalDateTime lastModified,
            Integer likes,
            Boolean isUpdated) {
        this.id = id;
        this.message = message;
        this.author = author;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.likes = likes;
        this.isUpdated = isUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Boolean getUpdated() {
        return isUpdated;
    }

    public void setUpdated(Boolean updated) {
        isUpdated = updated;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
