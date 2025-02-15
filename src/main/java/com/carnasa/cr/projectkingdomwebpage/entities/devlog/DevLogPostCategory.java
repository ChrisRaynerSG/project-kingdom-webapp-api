package com.carnasa.cr.projectkingdomwebpage.entities.devlog;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dev_log_post_categories", schema="project-kingdom-webapp-db")
public class DevLogPostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dev_log_post_category_id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "category", nullable = false, unique = true)
    private String category;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="last_modified", nullable = false)
    private LocalDateTime lastModified;

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
}

