package com.carnasa.cr.projectkingdomwebpage.models.devlog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DevlogPostDto {
    private Long id;
    private String category;
    private String title;
    private String message;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private Integer likes;
    private List<DevlogPostReplyDto> replies;
    private Boolean isUpdated;

    public DevlogPostDto(
            Long id,
            String category,
            String title,
            String message,
            String author,
            LocalDateTime createdAt,
            LocalDateTime lastModified,
            Integer likes,
            List<DevlogPostReplyDto> replies,
            Boolean isUpdated

                         ) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.message = message;
        this.author = author;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.likes = likes;
        this.replies = replies;
        this.isUpdated = isUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public List<DevlogPostReplyDto> getReplies() {
        return replies;
    }

    public void setReplies(List<DevlogPostReplyDto> replies) {
        this.replies = replies;
    }

    public Boolean getUpdated() {
        return isUpdated;
    }

    public void setUpdated(Boolean updated) {
        isUpdated = updated;
    }
}
