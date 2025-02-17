package com.carnasa.cr.projectkingdomwebpage.controllers.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.ForbiddenException;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostLikeDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostLikePutDto;
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

@RestController
public class DevLogPostController {

    public static Logger log = LoggerFactory.getLogger(DevLogPostController.class);

    public static final String BASE_URL = "/api/v1/project-kingdom";
    public static final String DEV_LOG_POST_URL = BASE_URL + "/devlogs";
    public static final String DEV_LOG_POST_URL_ID = DEV_LOG_POST_URL + "/{id}";
    public static final String DEV_LOG_POST_REPLY_URL = DEV_LOG_POST_URL_ID + "/replies";
    public static final String DEV_LOG_POST_LIKES_URL = DEV_LOG_POST_URL_ID + "/likes";
    public static final String DEV_LOG_POST_REPLY_URL_ID = DEV_LOG_POST_REPLY_URL + "/{reply}";
    public static final String DEV_LOG_POST_REPLY_URL_ID_LIKES = DEV_LOG_POST_REPLY_URL_ID + "/likes";
    public static final String DEV_LOG_POST_CATEGORY_URL = DEV_LOG_POST_URL + "/categories";

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
            DevLogPost devlogPost = devLogPostService.createDevLogPost(devlogPostPostDto, userId);
            return new ResponseEntity<>(DevLogUtils.toDto(devlogPost), HttpStatus.CREATED);
        }
        catch (RuntimeException e) {
            log.warn("Error posting DevLog post with error: {}", e.getMessage());
            throw new ForbiddenException("You must be logged in with Developer Role to create a Dev Log Post");
        }
    }

    @PutMapping(DEV_LOG_POST_LIKES_URL)
    public ResponseEntity<DevLogPostLikeDto> togglePostLike(@RequestBody DevLogPostLikePutDto like,
                                                            @PathVariable Long id){
        log.trace(PUT_ENDPOINT_LOG_HIT, DEV_LOG_POST_LIKES_URL);
        DevLogPostLikeDto likeCheck = devLogPostService.toggleDevLogPostLike(like, id);
        if(likeCheck==null){
            log.info("Removing post like");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // like is removed
        }
        else{
            log.info("Adding post like");
            return new ResponseEntity<>(likeCheck, HttpStatus.CREATED);
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
    @GetMapping(DEV_LOG_POST_URL)
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
     * @param id the id of the post
     * @return a JSON object containing information on a single post
     */
    @GetMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> getDevLogPostById(@PathVariable Long id) {
        log.trace(GET_ENDPOINT_LOG_HIT, DEV_LOG_POST_URL_ID);
        if(devLogPostService.getDevLogPostByIdDto(id).isEmpty()) {
            throw new NotFoundException("No posts found with id " + id);
        }
        return new ResponseEntity<>(devLogPostService.getDevLogPostByIdDto(id).get(),HttpStatus.OK);
    }

    @GetMapping(DEV_LOG_POST_LIKES_URL)
    public ResponseEntity<List<DevLogPostLikeDto>> getPostReplyLikes(@RequestParam(required = false) Integer page,
                                                                     @RequestParam(required = false) Integer size,
                                                                     @PathVariable Long id){
        log.trace(GET_ENDPOINT_LOG_HIT, DEV_LOG_POST_LIKES_URL);
        return new ResponseEntity<>(devLogPostService.getPostLikes(id,page,size).getContent(), HttpStatus.OK);
    }

    //Update

    @PatchMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> patchDevLogPost(@RequestBody DevLogPostPatchDto update, @PathVariable Long id) {
        log.trace(PATCH_ENDPOINT_LOG_HIT, DEV_LOG_POST_URL_ID);
        DevLogPostDto updatedPost = devLogPostService.updateDevLogPost(update, id);
        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    //Delete

    @DeleteMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> deleteDevLogPostById(@PathVariable Long id) {
        log.trace(DELETE_ENDPOINT_LOG_HIT, DEV_LOG_POST_URL_ID);
        devLogPostService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
