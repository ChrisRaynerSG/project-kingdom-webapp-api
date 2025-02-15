package com.carnasa.cr.projectkingdomwebpage.controllers;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.exceptions.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostReplyDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostReplyPostDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping(DEV_LOG_POST_URL)
    public ResponseEntity<List<DevLogPostDto>> getDevLogPosts(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Boolean isPopular,
            @RequestParam(required = false) String categoryName
    ) {
        List<DevLogPostDto> devLogPosts = devLogPostService
                .getDevLogPosts(page, pageSize, categoryName, search, startDate, endDate, isPopular, username).getContent();
        if(devLogPosts.isEmpty()) {
            throw new NotFoundException("No posts found with search parameters");
        }
        return new ResponseEntity<>(devLogPosts, HttpStatus.OK);
    }
}
