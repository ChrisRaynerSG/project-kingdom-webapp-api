package com.carnasa.cr.projectkingdomwebpage.entities.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dev_log_post_replies", schema="project-kingdom-webapp-db")
public class DevLogPostReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dev_log_post_reply_id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="dev_log_post_id",nullable = false)
    private DevLogPost post;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "is_updated")
    private Boolean isUpdated = false;

    @OneToMany
    private List<DevLogPostReplyLike> likes;

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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getUpdated() {
        return isUpdated;
    }

    public void setUpdated(Boolean updated) {
        isUpdated = updated;
    }

    public List<DevLogPostReplyLike> getLikes() {
        return likes;
    }

    public void setLikes(List<DevLogPostReplyLike> likes) {
        this.likes = likes;
    }
}
