package com.carnasa.cr.projectkingdomwebpage.models.forum;
import java.util.List;

public class ForumPostDto {

    private Long id;
    private String category;
    private String title;
    private String message;
    private String author;
    private List<ForumPostReplyDto> replies;
    private Integer likes;
}
