package com.carnasa.cr.projectkingdomwebpage.utils;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostReplyDto;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;

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
                        .map(DevLogUtils::replyToDto)
                        .toList() : null,
                devLogPost
                        .getUpdated()
        );
    }

    public static DevLogPostReplyDto replyToDto(DevLogPostReply devLogPostReply){
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
}
