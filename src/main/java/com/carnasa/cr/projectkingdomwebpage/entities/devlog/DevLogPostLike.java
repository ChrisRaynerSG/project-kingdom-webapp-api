package com.carnasa.cr.projectkingdomwebpage.entities.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dev_log_post_likes", schema="project-kingdom-webapp-db")
public class DevLogPostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dev_log_post_like_id", nullable = false, unique = true, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id",nullable=false)
    private UserEntity user;

    @Column(name="active")
    private Boolean active;

    @ManyToOne
    @JoinColumn(name="dev_log_post_id",nullable = false)
    private DevLogPost post;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public DevLogPost getPost() {
        return post;
    }

    public void setPost(DevLogPost post) {
        this.post = post;
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
