package com.carnasa.cr.projectkingdomwebpage.services.impl;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.*;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.BadRequestException;
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
import com.carnasa.cr.projectkingdomwebpage.repositories.devlog.*;
import com.carnasa.cr.projectkingdomwebpage.repositories.specifications.DevLogPostSpecification;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import com.carnasa.cr.projectkingdomwebpage.utils.ServiceUtils;
import jakarta.transaction.Transactional;
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
import java.util.stream.Collectors;

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

    public DevLogPostCategory createDevLogPostCategory(DevLogPostCategoryPostDto newCategory) {

        //validate Category name

        DevLogPostCategory devLogPostCategory = new DevLogPostCategory();
        devLogPostCategory.setCategory(newCategory.getCategory());
        devLogPostCategory.setCreatedAt(LocalDateTime.now());
        devLogPostCategory.setLastModified(LocalDateTime.now());

        return devLogPostCategoryRepository.save(devLogPostCategory);
    }

    //Read Methods



    /**
     * Method to populate drop down list of all categories
     */
    @Override
    public List<DevLogPostCategoryDto> getDevLogPostCategories() {
        return devLogPostCategoryRepository
                .findAll()
                .stream()
                .map(DevLogUtils::toDto)
                .collect(Collectors.toList());
    }

    public Optional<DevLogPostCategoryDto> getCategoryDto(Long id) {
        return devLogPostCategoryRepository.findById(id).map(DevLogUtils::toDto);
    }

    private Optional<DevLogPostCategory> getCategory(Long id){
        return devLogPostCategoryRepository.findById(id);
    }

    public Optional<DevLogPostCategory> getDevLogPostCategoryById(Long id) {
        return devLogPostCategoryRepository.findById(id);
    }

    public Optional<DevLogPostDto> getDevLogPostByIdDto(Long id) {
        return devLogPostRepository.findById(id).map(DevLogUtils::toDto);
    }

    private Optional<DevLogPost> getDevLogPostById(Long id){
        return devLogPostRepository.findById(id);
    }

    private Optional<DevLogPostReply> getDevLogPostReplyById(Long id){
        return devLogPostReplyRepository.findById(id);
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
    public Page<DevLogPostDto> getDevLogPosts(Integer page, Integer size, String category, String search, LocalDateTime startDate, LocalDateTime endDate, Boolean isPopular, String username, Boolean active) {
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);
        Specification<DevLogPost> spec = Specification
                .where(DevLogPostSpecification.getPostsByCategory(category))
                .and(DevLogPostSpecification.getPostsBySearch(search))
                .and(DevLogPostSpecification.getPostBetweenDates(startDate, endDate))
                .and(DevLogPostSpecification.getPostByPopularity(isPopular, startDate, endDate))
                .and(DevLogPostSpecification.getPostsByUserUsername(username))
                .and(DevLogPostSpecification.getPostByActive(active));

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
    @Override
    public Page<DevLogPostReplyDto> getPostReplies(Integer page, Integer size, Long postId, String username){
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);

        if(getDevLogPostById(postId).isEmpty()) {
            throw new NotFoundException("Post with ID: " + postId + " not found.");
        }

        //@todo add spec for filtering results
        //or not, might not be needed.

        return devLogPostReplyRepository.findAll(pageRequest).map(DevLogUtils::toDto);
    }

    @Override
    public Page<DevLogPostReplyDto> getPostReplies(Integer page, Integer size, String username) {
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);
        return devLogPostReplyRepository.findAll(pageRequest).map(DevLogUtils::toDto);
    }

    @Override
    public Optional<DevLogPostReplyDto> getPostReply(Long id) {
        return devLogPostReplyRepository.findById(id).map(DevLogUtils::toDto);
    }


    @Override
    public DevLogPostDto updateDevLogPost(DevLogPostPatchDto update, Long postId) {
        if(getDevLogPostById(postId).isEmpty()) {
            throw new NotFoundException("Post with ID: " + postId + " not found.");
        }
        DevLogPost post = getDevLogPostById(postId).get();
        boolean isUpdated = false;

        if(update.getCategoryId() != null) {
            Optional<DevLogPostCategory> newCategory = getCategory(update.getCategoryId());
            if(newCategory.isEmpty()) {
                throw new BadRequestException("Category with ID: " + update.getCategoryId() + " not found.");
            }
            else{
                post.setDevLogPostCategory(newCategory.get());
                isUpdated = true;
            }
        }
        if(update.getMessage()!= null){
            post.setMessage(update.getMessage());
            isUpdated = true;
        }
        if(update.getTitle()!= null){
            post.setTitle(update.getTitle());
            isUpdated = true;
        }
        if(update.getActive()!= null){
            post.setActive(update.getActive());
            isUpdated = true;
        }
        if(isUpdated){
            post.setUpdated(true);
            post.setLastModified(LocalDateTime.now());
            devLogPostRepository.save(post);
            return DevLogUtils.toDto(post);
        }
        else{
            return null;
        }
    }

    @Override
    public DevLogPostCategoryDto updateDevLogPostCategory(DevLogPostCategoryPatchDto update, Long categoryId) {
        if(getCategory(categoryId).isEmpty()) {
            throw new BadRequestException("Category with ID: " + categoryId + " not found.");
        }
        DevLogPostCategory category = getCategory(categoryId).get();

        if(update.getCategory() != null) {
            category.setCategory(update.getCategory());
            category.setLastModified(LocalDateTime.now());
            category = devLogPostCategoryRepository.save(category);
            return DevLogUtils.toDto(category);
        }
        else{
            return null;
        }
    }

    @Override
    public DevLogPostReplyDto updateDevLogPostReply(DevLogPostReplyPatchDto update, Long postId, Long replyId) {
        if(getDevLogPostReplyById(replyId).isEmpty()) {
            throw new BadRequestException("Reply with ID: " + postId + " not found.");
        }
        if(getDevLogPostById(postId).isEmpty()) {
            throw new BadRequestException("Post with ID: " + postId + " not found.");
        }
        DevLogPostReply reply = getDevLogPostReplyById(replyId).get();

        if(update.getMessage()!= null){
            reply.setMessage(update.getMessage());
            reply.setUpdated(true);
            reply.setLastModified(LocalDateTime.now());
            reply = devLogPostReplyRepository.save(reply);
            return DevLogUtils.toDto(reply);
        }
        else return null;
    }

    @Override
    public DevLogPostLikeDto toggleDevLogPostLike(DevLogPostLikePutDto toggleLike, Long postId) {

        if(userService.getUserById(toggleLike.getUserId()).isEmpty()) {
            throw new BadRequestException("Unable to like as user information unable to be retrieved");
        }

        if(getDevLogPostById(postId).isEmpty()) {
            throw new BadRequestException("Unable to like as post information unable to be retrieved");
        }

        DevLogPostLike like = new DevLogPostLike();
        Optional<DevLogPostLike> likeCheck = devLogPostLikeRepository.findByPostIdAndUserId(postId,toggleLike.getUserId());
        if(likeCheck.isPresent()) {
            devLogPostLikeRepository.delete(likeCheck.get());
            return null;
        }
        else{
            like.setPost(getDevLogPostById(postId).get());
            like.setUser(userService.getUserById(toggleLike.getUserId()).get());
            like.setCreatedAt(LocalDateTime.now());
            like.setLastModified(LocalDateTime.now());
            like.setActive(true);
            like = devLogPostLikeRepository.save(like);
            return DevLogUtils.toDto(like);
        }
    }

    @Override
    public DevLogPostLikeDto toggleDevLogPostReplyLike(DevLogPostLikePutDto toggleLike, Long postId, Long replyId) {

        if(userService.getUserById(toggleLike.getUserId()).isEmpty()) {
            throw new BadRequestException("Unable to like as user information unable to be retrieved");
        }
        if(getDevLogPostById(postId).isEmpty()) {
            throw new BadRequestException("Unable to like as post information unable to be retrieved");
        }
        if(getDevLogPostReplyById(replyId).isEmpty()) {
            throw new BadRequestException("Unable to like as reply information unable to be retrieved");
        }

        Optional<DevLogPostReplyLike> likeCheck = devLogPostReplyLikeRepository.findByUserIdAndReplyId(toggleLike.getUserId(), replyId);
        if(likeCheck.isPresent()) {
            devLogPostReplyLikeRepository.delete(likeCheck.get());
            return null;
        }
        else {
            DevLogPostReplyLike like = new DevLogPostReplyLike();
            like.setActive(true);
            like.setCreated(LocalDateTime.now());
            like.setLastModified(LocalDateTime.now());
            like.setUser(userService.getUserById(toggleLike.getUserId()).get());
            like.setReply(getDevLogPostReplyById(replyId).get());
            like = devLogPostReplyLikeRepository.save(like);
            return DevLogUtils.toDto(like);
        }
    }
    //Delete Methods


    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        if(getCategory(categoryId).isEmpty()) {
            throw new NotFoundException("Category with ID: " + categoryId + " not found.");
        }
        else{
            DevLogPostCategory category = getCategory(categoryId).get();
            List<DevLogPost> posts = devLogPostRepository.findAllByDevLogPostCategoryId(categoryId);
            if(!posts.isEmpty()) {
                for(DevLogPost post : posts) {
                    deletePost(post.getId());
                }
            }
            devLogPostCategoryRepository.delete(category);
        }
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Optional<DevLogPost> postCheck = devLogPostRepository.findById(postId);
        if(postCheck.isPresent()) {
            DevLogPost post = postCheck.get();
            if(!post.getLikes().isEmpty()) {
                devLogPostLikeRepository.deleteAll(post.getLikes());
            }
            if(!post.getReplies().isEmpty()) {
                for(DevLogPostReply reply : post.getReplies()) {
                    deleteReply(reply.getId());
                }
            }
            devLogPostRepository.delete(post);
        }
        else {
            throw new NotFoundException("Post with ID: " + postId + " not found.");
        }
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        Optional<DevLogPostReply> replyCheck = devLogPostReplyRepository.findById(replyId);
        if(replyCheck.isPresent()) {
            if(!replyCheck.get().getLikes().isEmpty()){
                devLogPostReplyLikeRepository.deleteAll(replyCheck.get().getLikes());
            }
            devLogPostReplyRepository.delete(replyCheck.get());
        }
        else{
            throw new NotFoundException("Reply with ID: " + replyId + " not found.");
        }
    }
}
