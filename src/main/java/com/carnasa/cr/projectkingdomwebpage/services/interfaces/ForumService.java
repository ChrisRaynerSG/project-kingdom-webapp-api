package com.carnasa.cr.projectkingdomwebpage.services.interfaces;

import com.carnasa.cr.projectkingdomwebpage.entities.forum.ForumPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public interface ForumService {

    //Read
    Optional<ForumPost> getForumPostById(Long id);
    Page<ForumPost> getForumPosts(Integer pageSize,
                                  Integer page,
                                  String category,
                                  String titleSearch,
                                  LocalDateTime startDate,
                                  LocalDateTime endDate,
                                  Integer Likes);

    //Create
    ForumPost saveForumPost(ForumPost forumPost);

    //Update
    ForumPost updateForumPost(ForumPost forumPost);

    //Delete
    void deleteForumPost(Long id);

}
