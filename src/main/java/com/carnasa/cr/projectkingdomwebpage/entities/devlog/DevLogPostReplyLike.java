package com.carnasa.cr.projectkingdomwebpage.entities.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dev_log_post_reply_likes", schema="project-kingdom-webapp-db")
public class DevLogPostReplyLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="dev_log_post_reply_like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name="active") // if user wants to unlike later
    private Boolean active;

    @Column(name="last_modified")
    private LocalDateTime lastModified;

    @Column(name="created_at")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "dev_log_post_reply_id")
    private DevLogPostReply reply;

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

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public DevLogPostReply getReply() {
        return reply;
    }

    public void setReply(DevLogPostReply reply) {
        this.reply = reply;
    }
}
