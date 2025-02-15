package com.carnasa.cr.projectkingdomwebpage.utils;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevlogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevlogPostReplyDto;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;

public class DevlogUtils {

    public static DevlogPostDto toDto(DevLogPost devLogPost) {
        return new DevlogPostDto(
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
                devLogPost
                        .getReplies()
                        .stream()
                        .map(DevlogUtils::replyToDto)
                        .toList(),
                devLogPost
                        .getUpdated()
        );
    }

    public static DevlogPostReplyDto replyToDto(DevLogPostReply devLogPostReply){
        return new DevlogPostReplyDto(
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
