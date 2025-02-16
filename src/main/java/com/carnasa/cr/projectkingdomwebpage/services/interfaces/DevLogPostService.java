package com.carnasa.cr.projectkingdomwebpage.services.interfaces;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostCategory;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostCategoryPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostReplyPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostCategoryDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostLikeDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostReplyDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostCategoryPatchDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostLikePutDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostPatchDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostReplyPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface DevLogPostService {

    //create
    DevLogPost createDevLogPost(DevLogPostPostDto devLogPostPostDto);
    DevLogPostReply createDevLogPostReply(DevLogPostReplyPostDto replyDto, Long postId);
    DevLogPostCategory createDevLogPostCategory(DevLogPostCategoryPostDto newCategory);

    //Read
    Optional<DevLogPostDto> getDevLogPostByIdDto(Long id);
    Page<DevLogPostDto> getDevLogPosts(Integer page, Integer size, String category, String search, LocalDateTime startDate, LocalDateTime endDate, Boolean isPopular, String username, Boolean active);

    Optional<DevLogPostCategoryDto> getCategoryDto(Long id);
    List<DevLogPostCategoryDto> getDevLogPostCategories();

    Optional<DevLogPostReplyDto> getPostReply(Long id);
    Page<DevLogPostReplyDto> getPostReplies(Integer page, Integer size, Long postId, String username, LocalDateTime startDate, LocalDateTime endDate);
    Page<DevLogPostReplyDto> getPostReplies(Integer page, Integer size, String username, LocalDateTime startDate, LocalDateTime endDate);

    Page<DevLogPostLikeDto> getPostLikes(Long id, Integer page, Integer size);
    Page<DevLogPostLikeDto> getReplyLikes(Long id, Integer page, Integer size);

    //Update
    DevLogPostDto updateDevLogPost(DevLogPostPatchDto update, Long postId);
    DevLogPostCategoryDto updateDevLogPostCategory(DevLogPostCategoryPatchDto update, Long categoryId);
    DevLogPostReplyDto updateDevLogPostReply(DevLogPostReplyPatchDto update, Long postId, Long replyId);

    DevLogPostLikeDto toggleDevLogPostLike(DevLogPostLikePutDto toggleLike, Long postId);
    DevLogPostLikeDto toggleDevLogPostReplyLike(DevLogPostLikePutDto toggleLike, Long postId, Long replyId);

    //Delete
    //@todo delete post, replies, categories
    void deleteCategory(Long categoryId);
    void deletePost(Long postId);
    void deleteReply(Long replyId);
}
