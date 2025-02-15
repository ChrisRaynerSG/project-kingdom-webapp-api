package com.carnasa.cr.projectkingdomwebpage.services.interfaces;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostPatchDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostReplyPostDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface DevLogPostService {

    //create

    DevLogPost createDevLogPost(DevLogPostPostDto devLogPostPostDto);
    DevLogPostReply createDevLogPostReply(DevLogPostReplyPostDto replyDto, Long postId);

    //Read
    Page<DevLogPostDto> getDevLogPosts(Integer page, Integer size, String category, String search, LocalDateTime startDate, LocalDateTime endDate, Boolean isPopular, String username);

    //Update
    DevLogPostDto updateDevLogPost(DevLogPostPatchDto update);

}
