package com.carnasa.cr.projectkingdomwebpage.entities.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="dev_log_post", schema="project-kingdom-webapp-db")
public class DevLogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="dev_log_post_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity creator;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "dev_log_post_category_id", nullable = false)
    private DevLogPostCategory devLogPostCategory;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "is_updated")
    private Boolean isUpdated = false;

    @OneToMany
    private List<DevLogPostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DevLogPostReply> replies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
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

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreationDate() {
        return createdAt;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.createdAt = creationDate;
    }

    public DevLogPostCategory getDevLogPostCategory() {
        return devLogPostCategory;
    }

    public void setDevLogPostCategory(DevLogPostCategory devLogPostCategory) {
        this.devLogPostCategory = devLogPostCategory;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<DevLogPostLike> getLikes() {
        return likes;
    }

    public void setLikes(List<DevLogPostLike> likes) {
        this.likes = likes;
    }

    public List<DevLogPostReply> getReplies() {
        return replies;
    }

    public void setReplies(List<DevLogPostReply> replies) {
        this.replies = replies;
    }

    public Boolean getUpdated() {
        return isUpdated;
    }

    public void setUpdated(Boolean updated) {
        isUpdated = updated;
    }
}
