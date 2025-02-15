package com.carnasa.cr.projectkingdomwebpage.entities.forum;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_posts", schema="project-kingdom-webapp-db")
public class ForumPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, updatable = false, name="forum_post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "forum_category_id", nullable = false)
    private ForumCategory forumCategory;

    @Column(name = "forum_post_title", nullable = false)
    private String title;

    @Column(name = "forum_post_message", nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false, updatable = false)
    private UserEntity user;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="last_modified", nullable = false)
    private LocalDateTime lastModified;
}
