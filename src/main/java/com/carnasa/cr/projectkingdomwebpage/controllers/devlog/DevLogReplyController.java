package com.carnasa.cr.projectkingdomwebpage.controllers.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.BadRequestException;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostReplyPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostLikeDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostReplyDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostReplyPatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import com.carnasa.cr.projectkingdomwebpage.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.carnasa.cr.projectkingdomwebpage.utils.LoggingUtils.*;
import static com.carnasa.cr.projectkingdomwebpage.utils.UrlUtils.*;

@RestController
public class DevLogReplyController {

    public static Logger log = LoggerFactory.getLogger(DevLogReplyController.class);
    private final DevLogPostService devLogPostService;

    @Autowired
    public DevLogReplyController(DevLogPostService devLogPostService) {
        log.trace("Creating DevLogReplyController");
        this.devLogPostService = devLogPostService;
    }

    //Create

    @PostMapping(DEV_LOG_POST_REPLY_URL)
    public ResponseEntity<DevLogPostReplyDto> postDevLogPostReply(@PathVariable Long postId,
                                                                  @RequestBody DevLogPostReplyPostDto postReply) {
        log.trace(POST_ENDPOINT_LOG_HIT,DEV_LOG_POST_REPLY_URL);
        DevLogPostReply savedReply = devLogPostService.createDevLogPostReply(postReply, postId);
        return new ResponseEntity<>(DevLogUtils.toDto(savedReply), HttpStatus.CREATED);
    }

    @PutMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostLikeDto> toggleReplyLike(
            @PathVariable Long postId,
            @PathVariable Long replyId){
        log.trace(PUT_ENDPOINT_LOG_HIT,DEV_LOG_POST_REPLY_URL_ID);
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            DevLogPostLikeDto likeCheck = devLogPostService.toggleDevLogPostReplyLike(userId, postId, replyId);
            if (likeCheck == null) {
                log.info("removing reply like");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                log.info("adding reply like");
                return new ResponseEntity<>(likeCheck, HttpStatus.CREATED);
            }
        }
        catch (RuntimeException e) {
            log.warn("Error posting DevLogReply like with error: {}", e.getMessage());
            throw new BadRequestException("Can not toggle post like, ERROR: " + e.getMessage());
        }
    }
    //Read
    /**
     *
     * @param postId Id of post to get replies of
     * @param page page number
     * @param size page size
     * @param search search parameters
     * @return list of all replies for a specific post
     */
    @GetMapping(DEV_LOG_POST_REPLY_URL)
    public ResponseEntity<List<DevLogPostReplyDto>> getDevLogPostReply(@PathVariable Long postId,
                                                                       @RequestParam(required = false) Integer page,
                                                                       @RequestParam(required = false) Integer size,
                                                                       @RequestParam(required = false) String search,
                                                                       @RequestParam(required = false) LocalDateTime startDate,
                                                                       @RequestParam(required = false) LocalDateTime endDate) {
        log.trace(GET_ENDPOINT_LOG_HIT,DEV_LOG_POST_REPLY_URL);
        Page<DevLogPostReplyDto> replies = devLogPostService.getPostReplies(page,size,postId,search,startDate,endDate);
        if(replies.isEmpty()) {
            throw new NotFoundException("No replies found for post with ID: " + postId);
        }
        return new ResponseEntity<>(replies.getContent(), HttpStatus.OK);
    }

    @GetMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostReplyDto> getDevLogPostReplyById(@PathVariable Long postId, @PathVariable Long replyId) {
        log.trace(GET_ENDPOINT_LOG_HIT,DEV_LOG_POST_REPLY_URL_ID);
        // more checks here to make sure post id and post id on reply match?

        if(devLogPostService.getPostReply(replyId).isEmpty()) {
            throw new NotFoundException("No replies found for post: " + postId  + "with id " + replyId);
        }
        return new ResponseEntity<>(devLogPostService.getPostReply(replyId).get(),HttpStatus.OK);
    }

    @GetMapping(DEV_LOG_POST_URL + "/replies")
    public ResponseEntity<List<DevLogPostReplyDto>> getAllReplies(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String search,
                                                                  @RequestParam(required = false) LocalDateTime startDate,
                                                                  @RequestParam(required = false) LocalDateTime endDate) {
        log.trace(GET_ENDPOINT_LOG_HIT,DEV_LOG_POST_URL+ "/replies");

        List<DevLogPostReplyDto> replies = devLogPostService.getPostReplies(page,size,search,startDate,endDate).getContent();
        if(replies.isEmpty()) {
            if(search==null){
                throw new NotFoundException("No replies found");
            }
            throw new NotFoundException("No replies found using search: " + search);
        }
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }

    @GetMapping(DEV_LOG_POST_REPLY_URL_ID_LIKES)
    public ResponseEntity<List<DevLogPostLikeDto>> getPostReplyLikes(@RequestParam(required = false) Integer page,
                                                                      @RequestParam(required = false) Integer size,
                                                                      @PathVariable Long replyId,
                                                                      @PathVariable Long postId){
        log.trace(GET_ENDPOINT_LOG_HIT,DEV_LOG_POST_REPLY_URL_ID_LIKES);
        return new ResponseEntity<>(devLogPostService.getReplyLikes(replyId,page,size).getContent(), HttpStatus.OK);
    }

    //Update

    @PatchMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostReplyDto> patchDevLogPostReply(@RequestBody DevLogPostReplyPatchDto update, @PathVariable Long postId, @PathVariable Long replyId) {
        log.trace(PATCH_ENDPOINT_LOG_HIT,DEV_LOG_POST_REPLY_URL_ID);
        DevLogPostReplyDto updatedPost = devLogPostService.updateDevLogPostReply(update, postId, replyId);
        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    //Delete

    @DeleteMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostReplyDto> deleteDevLogPostReplyById(@PathVariable Long replyId, @PathVariable Long postId) {
        log.trace(DELETE_ENDPOINT_LOG_HIT,DEV_LOG_POST_REPLY_URL_ID);
        devLogPostService.deleteReply(replyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
