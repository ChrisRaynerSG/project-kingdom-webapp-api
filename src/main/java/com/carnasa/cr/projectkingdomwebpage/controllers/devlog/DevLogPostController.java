package com.carnasa.cr.projectkingdomwebpage.controllers.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.BadRequestException;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.ForbiddenException;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostLikeDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostPatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import com.carnasa.cr.projectkingdomwebpage.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.carnasa.cr.projectkingdomwebpage.utils.LoggingUtils.*;
import static com.carnasa.cr.projectkingdomwebpage.utils.UrlUtils.*;

@RestController
public class DevLogPostController {

    public static Logger log = LoggerFactory.getLogger(DevLogPostController.class);

    private final DevLogPostService devLogPostService;

    @Autowired
    public DevLogPostController(DevLogPostService devLogPostService) {
        log.trace("Creating DevLogPostController");
        this.devLogPostService = devLogPostService;
    }

    //Create

    @PostMapping(DEV_LOG_POST_URL)
    public ResponseEntity<DevLogPostDto> postDevLogPost(@RequestBody DevLogPostPostDto devlogPostPostDto) {
        log.trace(POST_ENDPOINT_LOG_HIT, DEV_LOG_POST_URL);
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            if(SecurityUtils.hasDevLogPostPermission()) {
                DevLogPost devlogPost = devLogPostService.createDevLogPost(devlogPostPostDto, userId);
                return new ResponseEntity<>(DevLogUtils.toDto(devlogPost), HttpStatus.CREATED);
            }
            else{
                log.info("User does not have permission to post DevLog post");
                throw new ForbiddenException("You must be logged in with Developer Role to create a Dev Log Post");
            }
        }
        catch (RuntimeException e) {
            log.warn("Error posting DevLog post with error: {}", e.getMessage());
            throw new ForbiddenException("You must be logged in with Developer Role to create a Dev Log Post");
        }
    }

    @PutMapping(DEV_LOG_POST_LIKES_URL)
    public ResponseEntity<DevLogPostLikeDto> togglePostLike(@PathVariable Long postId){
        log.trace(PUT_ENDPOINT_LOG_HIT, DEV_LOG_POST_LIKES_URL);
        try {
            UUID userId = SecurityUtils.getCurrentUserId();
            DevLogPostLikeDto likeCheck = devLogPostService.toggleDevLogPostLike(userId, postId);
            if (likeCheck == null) {
                log.info("Removing post like");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // like is removed
            } else {
                log.info("Adding post like");
                return new ResponseEntity<>(likeCheck, HttpStatus.CREATED);
            }
        }
        catch (RuntimeException e) {
            log.warn("Error posting DevLog like with error: {}", e.getMessage());
            throw new BadRequestException("Can not toggle post like, ERROR: " + e.getMessage());
        }
    }

    //Read
    /**
     *
     * @param pageSize nullable, defaults to 25, amount of results returned per page
     * @param page nullable, defaults to 0, page number
     * @param startDate nullable - startDate for searching by createdDate
     * @param endDate nullable - endDate for searching by createdDate
     * @param search nullable - search parameter, to search by author, content and title
     * @param username nullable - strict equals search to find all by specific username
     * @param isPopular nullable - boolean toggle to determine whether results should be sorted by popularity
     * @param categoryName nullable - strict equals search to find all by specific category
     * @return a filtered list of DevLog posts
     */
    @GetMapping(DEV_LOG_POST_URL) // this should be accessible to all
    public ResponseEntity<List<DevLogPostDto>> getDevLogPosts(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Boolean isPopular,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false, defaultValue = "true") boolean isActive
    ) {
        log.trace(GET_ENDPOINT_LOG_HIT, DEV_LOG_POST_URL);
        List<DevLogPostDto> devLogPosts = devLogPostService
                .getDevLogPosts(page, pageSize, categoryName, search, startDate, endDate, isPopular, username, isActive).getContent();
        if(devLogPosts.isEmpty()) {
            throw new NotFoundException("No posts found with search parameters");
        }
        return new ResponseEntity<>(devLogPosts, HttpStatus.OK);
    }

    /**
     * @param postId the id of the post
     * @return a JSON object containing information on a single post
     */
    @GetMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> getDevLogPostById(@PathVariable Long postId) {
        log.trace(GET_ENDPOINT_LOG_HIT, DEV_LOG_POST_URL_ID);
        if(devLogPostService.getDevLogPostByIdDto(postId).isEmpty()) {
            throw new NotFoundException("No posts found with id " + postId);
        }
        return new ResponseEntity<>(devLogPostService.getDevLogPostByIdDto(postId).get(),HttpStatus.OK);
    }

    @GetMapping(DEV_LOG_POST_LIKES_URL)
    public ResponseEntity<List<DevLogPostLikeDto>> getPostReplyLikes(@RequestParam(required = false) Integer page,
                                                                     @RequestParam(required = false) Integer size,
                                                                     @PathVariable Long postId){
        log.trace(GET_ENDPOINT_LOG_HIT, DEV_LOG_POST_LIKES_URL);
        return new ResponseEntity<>(devLogPostService.getPostLikes(postId,page,size).getContent(), HttpStatus.OK);
    }

    //Update

    @PatchMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> patchDevLogPost(@RequestBody DevLogPostPatchDto update, @PathVariable Long postId) {
        log.trace(PATCH_ENDPOINT_LOG_HIT, DEV_LOG_POST_URL_ID);

        UUID userID = SecurityUtils.getCurrentUserId();

        DevLogPostDto updatedPost = devLogPostService.updateDevLogPost(update, postId);

        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    //Delete

    @DeleteMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> deleteDevLogPostById(@PathVariable Long postId) {
        log.trace(DELETE_ENDPOINT_LOG_HIT, DEV_LOG_POST_URL_ID);

        devLogPostService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
