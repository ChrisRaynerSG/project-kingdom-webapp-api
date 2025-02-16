package com.carnasa.cr.projectkingdomwebpage.controllers.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostCategory;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
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
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class DevLogPostController {

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
        this.devLogPostService = devLogPostService;
    }

    //Create

    @PostMapping(DEV_LOG_POST_URL)
    public ResponseEntity<DevLogPostDto> postDevLogPost(@RequestBody DevLogPostPostDto devlogPostPostDto) {

        DevLogPost devlogPost = devLogPostService.createDevLogPost(devlogPostPostDto);
        return new ResponseEntity<>(DevLogUtils.toDto(devlogPost), HttpStatus.CREATED);
    }

    @PutMapping(DEV_LOG_POST_LIKES_URL)
    public ResponseEntity<DevLogPostLikeDto> togglePostLike(@RequestBody DevLogPostLikePutDto like,
                                                            @PathVariable Long id){
        DevLogPostLikeDto likeCheck = devLogPostService.toggleDevLogPostLike(like, id);
        if(likeCheck==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // like is removed
        }
        else{
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
        if(devLogPostService.getDevLogPostByIdDto(id).isEmpty()) {
            throw new NotFoundException("No posts found with id " + id);
        }
        return new ResponseEntity<>(devLogPostService.getDevLogPostByIdDto(id).get(),HttpStatus.OK);
    }

    @GetMapping(DEV_LOG_POST_LIKES_URL)
    public ResponseEntity<List<DevLogPostLikeDto>> getPostReplyLikes(@RequestParam(required = false) Integer page,
                                                                     @RequestParam(required = false) Integer size,
                                                                     @PathVariable Long id){
        return new ResponseEntity<>(devLogPostService.getPostLikes(id,page,size).getContent(), HttpStatus.OK);
    }

    //Update

    @PatchMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> patchDevLogPost(@RequestBody DevLogPostPatchDto update, @PathVariable Long id) {
        DevLogPostDto updatedPost = devLogPostService.updateDevLogPost(update, id);
        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    //Delete

    @DeleteMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> deleteDevLogPostById(@PathVariable Long id) {
        devLogPostService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
