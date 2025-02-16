package com.carnasa.cr.projectkingdomwebpage.controllers.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostReplyPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostLikeDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostReplyDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostLikePutDto;
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

import static com.carnasa.cr.projectkingdomwebpage.controllers.devlog.DevLogPostController.*;

@RestController
public class DevLogReplyController {

    private final DevLogPostService devLogPostService;

    @Autowired
    public DevLogReplyController(DevLogPostService devLogPostService) {
        this.devLogPostService = devLogPostService;
    }

    //Create

    @PostMapping(DEV_LOG_POST_REPLY_URL)
    public ResponseEntity<DevLogPostReplyDto> postDevLogPostReply(@PathVariable Long id,
                                                                  @RequestBody DevLogPostReplyPostDto postReply) {
        DevLogPostReply savedReply = devLogPostService.createDevLogPostReply(postReply, id);
        return new ResponseEntity<>(DevLogUtils.toDto(savedReply), HttpStatus.CREATED);
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
    //Read
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
                                                                       @RequestParam(required = false) String search,
                                                                       @RequestParam(required = false) LocalDateTime startDate,
                                                                       @RequestParam(required = false) LocalDateTime endDate) {
        Page<DevLogPostReplyDto> replies = devLogPostService.getPostReplies(page,size,id,search,startDate,endDate);
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

    @GetMapping(DEV_LOG_POST_URL + "/replies")
    public ResponseEntity<List<DevLogPostReplyDto>> getAllReplies(@RequestParam(required = false) Integer page,
                                                                  @RequestParam(required = false) Integer size,
                                                                  @RequestParam(required = false) String search,
                                                                  @RequestParam(required = false) LocalDateTime startDate,
                                                                  @RequestParam(required = false) LocalDateTime endDate) {

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
                                                                      @PathVariable Long reply,
                                                                      @PathVariable Long id){
        return new ResponseEntity<>(devLogPostService.getReplyLikes(reply,page,size).getContent(), HttpStatus.OK);
    }

    //Update

    @PatchMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostReplyDto> patchDevLogPostReply(@RequestBody DevLogPostReplyPatchDto update, @PathVariable Long id, @PathVariable Long reply) {
        DevLogPostReplyDto updatedPost = devLogPostService.updateDevLogPostReply(update, id, reply);
        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    //Delete

    @DeleteMapping(DEV_LOG_POST_REPLY_URL_ID)
    public ResponseEntity<DevLogPostReplyDto> deleteDevLogPostReplyById(@PathVariable Long reply, @PathVariable Long id) {
        devLogPostService.deleteReply(reply);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
