package com.carnasa.cr.projectkingdomwebpage.controllers;

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

    @PostMapping(DEV_LOG_POST_URL)
    public ResponseEntity<DevLogPostDto> postDevLogPost(@RequestBody DevLogPostPostDto devlogPostPostDto) {

        DevLogPost devlogPost = devLogPostService.createDevLogPost(devlogPostPostDto);
        return new ResponseEntity<>(DevLogUtils.toDto(devlogPost), HttpStatus.CREATED);
    }

    @PostMapping(DEV_LOG_POST_REPLY_URL)
    public ResponseEntity<DevLogPostReplyDto> postDevLogPostReply(@PathVariable Long id,
                                                                  @RequestBody DevLogPostReplyPostDto postReply) {
        DevLogPostReply savedReply = devLogPostService.createDevLogPostReply(postReply, id);
        return new ResponseEntity<>(DevLogUtils.toDto(savedReply), HttpStatus.CREATED);
    }

    @PostMapping(DEV_LOG_POST_CATEGORY_URL)
    public ResponseEntity<DevLogPostCategoryDto> postDevLogPostCategory(@RequestBody DevLogPostCategoryPostDto newCategory) {
        DevLogPostCategory savedCategory = devLogPostService.createDevLogPostCategory(newCategory);
        return new ResponseEntity<>(DevLogUtils.toDto(savedCategory), HttpStatus.CREATED);
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

    @PutMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostLikeDto> toggleReplyLike(@RequestBody DevLogPostLikePutDto like,
                                                                  @PathVariable Long id,
                                                                  @PathVariable Long reply){
        DevLogPostLikeDto likeCheck = devLogPostService.toggleDevLogPostReplyLike(like, id, reply);
        if(likeCheck==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            return new ResponseEntity<>(likeCheck, HttpStatus.CREATED);
        }
    }

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

    /**
     *
     * @param id
     * @param page
     * @param size
     * @param search
     * @return list of all replies for a specific post
     */
    @GetMapping(DEV_LOG_POST_REPLY_URL)
    public ResponseEntity<List<DevLogPostReplyDto>> getDevLogPostReply(@PathVariable Long id,
                                                                       @RequestParam(required = false) Integer page,
                                                                       @RequestParam(required = false) Integer size,
                                                                       @RequestParam(required = false) String search) {
        Page<DevLogPostReplyDto> replies = devLogPostService.getPostReplies(page,size,id,search);
        if(replies.isEmpty()) {
            throw new NotFoundException("No replies found for post with ID: " + id);
        }
        return new ResponseEntity<>(replies.getContent(), HttpStatus.OK);
    }

    @GetMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostReplyDto> getDevLogPostReplyById(@PathVariable Long id, @PathVariable Long reply) {

        // more checks here to make sure post id and post id on reply match?

        if(devLogPostService.getPostReply(reply).isEmpty()) {
            throw new NotFoundException("No replies found for post: " + id  + "with id " + reply);
        }
        return new ResponseEntity<>(devLogPostService.getPostReply(reply).get(),HttpStatus.OK);
    }


    /**
     * @return all categories for the Dev Log entity/model
     */
    @GetMapping(DEV_LOG_POST_CATEGORY_URL)
    public ResponseEntity<List<DevLogPostCategoryDto>> getDevLogCategories() {
        return new ResponseEntity<>(devLogPostService.getDevLogPostCategories(), HttpStatus.OK);
    }

    /**
     *
     * @param id id of the category
     * @return single instance of a Dev Log category
     */
    @GetMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> getDevLogPostCategory(@PathVariable Long id) {
        if(devLogPostService.getCategoryDto(id).isEmpty()) {
            throw new NotFoundException("No posts found with id " + id);
        }
        return new ResponseEntity<>(devLogPostService.getCategoryDto(id).get(),HttpStatus.OK);
    }

    @PatchMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> patchDevLogPost(@RequestBody DevLogPostPatchDto update, @PathVariable Long id) {
        DevLogPostDto updatedPost = devLogPostService.updateDevLogPost(update, id);
        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @PatchMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> patchDevLogPostCategory(@RequestBody DevLogPostCategoryPatchDto update, @PathVariable Long id) {
        DevLogPostCategoryDto updatedPost = devLogPostService.updateDevLogPostCategory(update, id);
        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @PatchMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostReplyDto> patchDevLogPostReply(@RequestBody DevLogPostReplyPatchDto update, @PathVariable Long id, @PathVariable Long reply) {
        DevLogPostReplyDto updatedPost = devLogPostService.updateDevLogPostReply(update, id, reply);
        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }
    @DeleteMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> deleteDevLogPostCategory(@PathVariable Long id) {
        devLogPostService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping(DEV_LOG_POST_URL_ID)
    public ResponseEntity<DevLogPostDto> deleteDevLogPostById(@PathVariable Long id) {
        devLogPostService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostReplyDto> deleteDevLogPostReplyById(@PathVariable Long reply, @PathVariable Long id) {
        devLogPostService.deleteReply(reply);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
