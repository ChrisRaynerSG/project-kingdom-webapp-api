package com.carnasa.cr.projectkingdomwebpage.utils;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.*;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostCategoryDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostLikeDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostReplyDto;

import java.util.Set;

public class DevLogUtils {


    public static DevLogPostDto toDto(DevLogPost devLogPost) {
        return new DevLogPostDto(
                devLogPost
                        .getId(),
                devLogPost
                        .getDevLogPostCategory()
                        .getCategory(),
                devLogPost
                        .getTitle(),
                devLogPost
                        .getMessage(),
                devLogPost
                        .getCreator()
                        .getUsername(),
                devLogPost
                        .getCreationDate(),
                devLogPost
                        .getLastModified(),
                devLogPost
                        .getLikes()
                        .size(),
                !devLogPost
                        .getReplies().isEmpty() ?
                        devLogPost.getReplies().stream()
                        .map(DevLogUtils::toDto)
                        .toList() : null,
                devLogPost
                        .getUpdated()
        );
    }

    public static DevLogPostReplyDto toDto(DevLogPostReply devLogPostReply){
        return new DevLogPostReplyDto(
                devLogPostReply.getId(),
                devLogPostReply.getMessage(),
                devLogPostReply.getUser().getUsername(),
                devLogPostReply.getCreatedAt(),
                devLogPostReply.getLastModified(),
                devLogPostReply.getLikes().size(),
                devLogPostReply.getUpdated()
        );
    }

    public static DevLogPostCategoryDto toDto(DevLogPostCategory devLogPostCategory) {
        return new DevLogPostCategoryDto(
                devLogPostCategory.getId(),
                devLogPostCategory.getCategory()
        );
    }

    public static DevLogPostLikeDto toDto(DevLogPostLike devLogPostLike) {
        return new DevLogPostLikeDto(
                devLogPostLike.getUser().getUsername(),
                devLogPostLike.getCreatedAt()
        );
    }
    public static DevLogPostLikeDto toDto(DevLogPostReplyLike like) {
        return new DevLogPostLikeDto(
                like.getUser().getUsername(),
                like.getCreated()
        );
    }
}
