package com.carnasa.cr.projectkingdomwebpage.controllers;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.exceptions.BadRequestException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostReplyDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostReplyPostDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class DevLogPostController {

    public static final String DEV_LOG_POST_URL = "/api/v1/project-kingdom/devlogs";
    public static final String DEV_LOG_POST_URL_ID = DEV_LOG_POST_URL + "/{id}";
    public static final String DEV_LOG_POST_REPLY_URL = DEV_LOG_POST_URL_ID + "/replies";
    public static final String DEV_LOG_POST_REPLY_URL_ID = DEV_LOG_POST_REPLY_URL + "/{reply}";

    private final DevLogPostService devLogPostService;

    @Autowired
    public DevLogPostController(DevLogPostService devLogPostService) {
        this.devLogPostService = devLogPostService;
    }

    @PostMapping(DEV_LOG_POST_URL)
    public ResponseEntity<DevLogPostDto> postDevLogPost(@RequestBody DevLogPostPostDto devlogPostPostDto) {

        DevLogPost devlogPost = devLogPostService.createDevLogPost(devlogPostPostDto);
        return new ResponseEntity<>(DevLogUtils.toDto(devlogPost), HttpStatus.CREATED);
    }

    @PostMapping(DEV_LOG_POST_REPLY_URL)
    public ResponseEntity<DevLogPostReplyDto> postDevLogPostReply(@PathVariable Long id,
                                                                  @RequestBody DevLogPostReplyPostDto postReply) {
        DevLogPostReply savedReply = devLogPostService.createDevLogPostReply(postReply, id);
        return new ResponseEntity<>(DevLogUtils.replyToDto(savedReply), HttpStatus.CREATED);
    }
}
