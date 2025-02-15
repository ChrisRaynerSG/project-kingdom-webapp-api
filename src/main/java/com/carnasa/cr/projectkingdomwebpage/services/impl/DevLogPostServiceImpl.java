package com.carnasa.cr.projectkingdomwebpage.services.impl;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostCategory;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.exceptions.BadRequestException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.*;
import com.carnasa.cr.projectkingdomwebpage.repositories.devlog.*;
import com.carnasa.cr.projectkingdomwebpage.repositories.specifications.DevLogPostSpecification;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import com.carnasa.cr.projectkingdomwebpage.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * This class serves as the implementation for the service layer for any actions relating to DevLogs
 *
 */
@Service
public class DevLogPostServiceImpl implements DevLogPostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevLogPostServiceImpl.class);

    private final DevLogPostRepository devLogPostRepository;
    private final DevLogPostCategoryRepository devLogPostCategoryRepository;
    private final DevLogPostLikeRepository devLogPostLikeRepository;
    private final DevLogPostReplyRepository devLogPostReplyRepository;
    private final DevLogPostReplyLikeRepository devLogPostReplyLikeRepository;
    private final UserService userService;

    /**
     * Constructor for DevLogServiceImpl
     * @param devLogPostRepository
     * @param devLogPostCategoryRepository
     * @param devLogPostLikeRepository
     * @param devLogPostReplyRepository
     * @param devLogPostReplyLikeRepository
     * @param userService
     */

    @Autowired
    public DevLogPostServiceImpl(DevLogPostRepository devLogPostRepository,
                                 DevLogPostCategoryRepository devLogPostCategoryRepository,
                                 DevLogPostLikeRepository devLogPostLikeRepository,
                                 DevLogPostReplyRepository devLogPostReplyRepository,
                                 DevLogPostReplyLikeRepository devLogPostReplyLikeRepository,
                                 UserService userService) {

        this.devLogPostRepository = devLogPostRepository;
        this.devLogPostCategoryRepository = devLogPostCategoryRepository;
        this.devLogPostLikeRepository = devLogPostLikeRepository;
        this.devLogPostReplyRepository = devLogPostReplyRepository;
        this.devLogPostReplyLikeRepository = devLogPostReplyLikeRepository;
        this.userService = userService;
    }

    //Create Methods
    @Override
    public DevLogPost createDevLogPost(DevLogPostPostDto devLogPostPostDto) {

        DevLogPost devLogPost = new DevLogPost();

        // validation for title and message content

        // validate dto components
        if(getDevLogPostCategoryById(devLogPostPostDto.getCategoryId()).isPresent()) {
            devLogPost.setDevLogPostCategory(getDevLogPostCategoryById(devLogPostPostDto.getCategoryId()).get());
        }
        if(userService.getUserById(devLogPostPostDto.getUserId()).isPresent()) {
            devLogPost.setCreator(userService.getUserById(devLogPostPostDto.getUserId()).get());
        }
        else{
            throw new BadRequestException("Unable to create post as user information unable to be retrieved");
        }

        devLogPost.setMessage(devLogPostPostDto.getMessage());
        devLogPost.setTitle(devLogPostPostDto.getTitle());
        devLogPost.setCreationDate(LocalDateTime.now());
        devLogPost.setLastModified(LocalDateTime.now());

        devLogPost.setActive(true);

        DevLogPost savedPost = devLogPostRepository.save(devLogPost);
        devLogPostRepository.flush();
        return savedPost;
    }

    public DevLogPostReply createDevLogPostReply(DevLogPostReplyPostDto replyDto, Long postId) {

        DevLogPostReply devLogPostReply = new DevLogPostReply();
        devLogPostReply.setMessage(replyDto.getMessage());

        //properly set the relationships
        if(getDevLogPostById(postId).isEmpty()) {
            throw new BadRequestException("Post with ID: " + postId + " does not exist");
        }
        else {
            devLogPostReply.setPost(getDevLogPostById(postId).get());
        }
        if(userService.getUserById(replyDto.getUserId()).isEmpty()) {
            throw new BadRequestException("Unable to create post as user information unable to be retrieved");
        }
        else{
            devLogPostReply.setUser(userService.getUserById(replyDto.getUserId()).get());
        }

        devLogPostReply.setActive(true);
        devLogPostReply.setCreatedAt(LocalDateTime.now());
        devLogPostReply.setLastModified(LocalDateTime.now());
        devLogPostReply.setUpdated(false);

        return devLogPostReplyRepository.save(devLogPostReply);
    }


    //Read Methods

    /**
     * Method to populate drop down list of all categories
     */
    public List<DevLogPostCategory> getDevLogPostCategories() {
        return devLogPostCategoryRepository.findAll();
    }

    public Optional<DevLogPostCategory> getDevLogPostCategoryById(Long id) {
        return devLogPostCategoryRepository.findById(id);
    }

    public Optional<DevLogPost> getDevLogPostById(Long id) {
        return devLogPostRepository.findById(id);
    }

    /**
     * Method to get DevLogPosts filtered by parameters
     * @param page
     * @param size
     * @param category
     * @param search
     * @param startDate
     * @param endDate
     * @param isPopular
     * @param username
     * @return A page of DevLogPostDtos
     */
    @Override
    public Page<DevLogPostDto> getDevLogPosts(Integer page, Integer size, String category, String search, LocalDateTime startDate, LocalDateTime endDate, Boolean isPopular, String username) {
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);
        Specification<DevLogPost> spec = Specification
                .where(DevLogPostSpecification.getPostsByCategory(category))
                .and(DevLogPostSpecification.getPostsBySearch(search))
                .and(DevLogPostSpecification.getPostBetweenDates(startDate, endDate))
                .and(DevLogPostSpecification.getPostByPopularity(isPopular, startDate, endDate))
                .and(DevLogPostSpecification.getPostsByUserUsername(username));

        return devLogPostRepository.findAll(spec, pageRequest).map(DevLogUtils::toDto);
    }

    /**
     *
     * @param page
     * @param size
     * @param postId the id of the post
     * @param username username if searching a users replies
     * @return
     */

    public Page<DevLogPostReplyDto> getDevLogPostReplies(Integer page, Integer size, Long postId, String username){
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);

        //@todo add spec for filtering results

        return devLogPostReplyRepository.findAll(pageRequest).map(DevLogUtils::replyToDto);
    }

    //Update Methods

    @Override
    public DevLogPostDto updateDevLogPost(DevLogPostPatchDto update) {
        return null;
    }


    //Delete Methods
}
