package com.carnasa.cr.projectkingdomwebpage.models.forum;

import java.time.LocalDateTime;

public class ForumPostReplyDto {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String author;
    private Integer likes;
}
