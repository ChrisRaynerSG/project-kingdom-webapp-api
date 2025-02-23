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
import com.carnasa.cr.projectkingdomwebpage.repositories.specifications.DevLogPostReplySpecification;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class serves as the implementation for the service layer for any actions relating to DevLogs, encompassing all CRUD methods
 * @author Christopher Rayner
 * @see com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService
 */
@Service
public class DevLogPostServiceImpl implements DevLogPostService {

    public static Logger log = LoggerFactory.getLogger(DevLogPostServiceImpl.class);

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

        log.trace("Creating DevLogPostService");
        this.devLogPostRepository = devLogPostRepository;
        this.devLogPostCategoryRepository = devLogPostCategoryRepository;
        this.devLogPostLikeRepository = devLogPostLikeRepository;
        this.devLogPostReplyRepository = devLogPostReplyRepository;
        this.devLogPostReplyLikeRepository = devLogPostReplyLikeRepository;
        this.userService = userService;
    }

    //Create Methods

    /**
     * @param devLogPostPostDto DevLogPostPostDto object containing title, message, categoryId and userId
     * @return a newly created DevLogPost object
     * @throws BadRequestException if userId provided does not exist
     * @throws BadRequestException if unable to save post to database
     * @see BadRequestException
     */
    @Override
    public DevLogPost createDevLogPost(DevLogPostPostDto devLogPostPostDto, UUID userId) {

        log.trace("Attempting to create a DevLogPost");
        DevLogPost devLogPost = new DevLogPost();

        //@todo validation for title and message content

        // validate dto components
        if(getDevLogPostCategoryById(devLogPostPostDto.getCategoryId()).isPresent()) {
            log.trace("Setting up post category relationship");
            devLogPost.setDevLogPostCategory(getDevLogPostCategoryById(devLogPostPostDto.getCategoryId()).get());
        }
        if(userService.getUserById(userId).isPresent()) {
            log.trace("Setting up user relationship");
            devLogPost.setCreator(userService.getUserById(userId).get());
        }
        else{
            log.warn("User information with ID: {} does not exist ", userId);
            throw new BadRequestException("Unable to create post as user information unable to be retrieved");
        }

        log.trace("Setting up post message, title, createdAt, lastModified, active");

        devLogPost.setMessage(devLogPostPostDto.getMessage());
        devLogPost.setTitle(devLogPostPostDto.getTitle());
        devLogPost.setCreationDate(LocalDateTime.now());
        devLogPost.setLastModified(LocalDateTime.now());

        devLogPost.setActive(true);
        log.info("Attempting to save new post at: {} \n Title: {}", devLogPost.getCreationDate(), devLogPost.getTitle());
        try {
            DevLogPost savedPost = devLogPostRepository.save(devLogPost);
            devLogPostRepository.flush();
            log.info("Saved post with ID: {}", savedPost.getId());
            return savedPost;
        }
        catch(Exception e) {
            log.error("Unable to save new post to the database with error: {}", e.getMessage());
            throw new BadRequestException("Unable to save post to the database with error: " + e.getMessage());
        }
    }
    /**
     * @param replyDto DevLogPostReplyPostDto object containing userId and message
     * @param postId id of post that will parent reply
     * @return saves to database and returns newly created reply
     * @throws BadRequestException if unable to retrieve user or post based on Id provided
     * @throws BadRequestException if unable to save reply to database
     */
    @Override
    public DevLogPostReply createDevLogPostReply(DevLogPostReplyPostDto replyDto, Long postId) {

        log.trace("Attempting to create a new DevLogPost reply");

        DevLogPostReply devLogPostReply = new DevLogPostReply();

        //properly set the relationships
        if(getDevLogPostById(postId).isEmpty()) {
            log.warn("Post with ID: {} does not exist, unable to create new post reply on a non existent post.", postId);
            throw new BadRequestException("Post with ID: " + postId + " does not exist");
        }
        else {
            log.trace("Setting up post relationship");
            devLogPostReply.setPost(getDevLogPostById(postId).get());
        }
        if(userService.getUserById(replyDto.getUserId()).isEmpty()) {
            log.warn("User information with ID: {} does not exist, unable to create a new reply with a non existent user.", replyDto.getUserId());
            throw new BadRequestException("Unable to create post as user information unable to be retrieved");
        }
        else{
            log.trace("Setting up user relationship");
            devLogPostReply.setUser(userService.getUserById(replyDto.getUserId()).get());
        }

        log.trace("Setting up post message, title, createdAt, lastModified, active, updated");
        devLogPostReply.setMessage(replyDto.getMessage());
        devLogPostReply.setActive(true);
        devLogPostReply.setCreatedAt(LocalDateTime.now());
        devLogPostReply.setLastModified(LocalDateTime.now());
        devLogPostReply.setUpdated(false);

        log.info("Attempting to save new reply to database at: {} \n Message: {}", devLogPostReply.getCreatedAt(), devLogPostReply.getMessage());
        try {
            DevLogPostReply reply = devLogPostReplyRepository.save(devLogPostReply);
            log.info("Created new reply with ID: {}", reply.getId());
            return reply;
        }
        catch(Exception e) {
            log.error("Unable to save reply to the database with error: {}", e.getMessage());
            throw new BadRequestException("Unable to save reply to the database with error: " + e.getMessage());
        }
    }
    /**
     * @param newCategory DevLogPostCategoryPostDto object containing string of category to be created
     * @return new DevLogPostCategory object that has been saved to database
     * @throws BadRequestException if unable to save category to the database
     */
    @Override
    public DevLogPostCategory createDevLogPostCategory(DevLogPostCategoryPostDto newCategory) {

        log.trace("Attempting to create a new DevLogPostCategory");

        //validate Category name
        DevLogPostCategory devLogPostCategory = new DevLogPostCategory();

        log.trace("setting up category, createdAt, lastModified");
        devLogPostCategory.setCategory(newCategory.getCategory());
        devLogPostCategory.setCreatedAt(LocalDateTime.now());
        devLogPostCategory.setLastModified(LocalDateTime.now());

        log.info("Attempting to save to the database new category: {} \nAt time: {}", devLogPostCategory.getCategory(), devLogPostCategory.getCreatedAt());
        try {
            DevLogPostCategory savedCategory = devLogPostCategoryRepository.save(devLogPostCategory);
            log.info("Saved new category with ID: {}", savedCategory.getId());
            return savedCategory;
        }
        catch(Exception e) {
            log.error("Unable to save category to the database with error: {}", e.getMessage());
            throw new BadRequestException("Unable to save category to the database with error: " + e.getMessage());
        }
    }

    //Read Methods

    /**
     * Method to populate drop down list of all categories
     * @return A List of DevLogPostCategoryDto containing the Id and name of the category
     */
    @Override
    public List<DevLogPostCategoryDto> getDevLogPostCategories() {
        log.trace("Attempting to get all DevLogPostCategories");
        return devLogPostCategoryRepository
                .findAll()
                .stream()
                .map(DevLogUtils::toDto)
                .collect(Collectors.toList());
    }
    /**
     * @param id id of category to return
     * @return Optional of DevLogPostCategoryDto containing the Id and name of the category
     */
    @Override
    public Optional<DevLogPostCategoryDto> getCategoryDto(Long id) {

        return devLogPostCategoryRepository.findById(id).map(DevLogUtils::toDto);
    }
    /**
     * @param id id of category to return
     * @return Optional of DevLogPostCategory entity
     */
    public Optional<DevLogPostCategory> getDevLogPostCategoryById(Long id) {
        log.trace("Attempting to get DevLogPostCategory by ID: {}", id);
        return devLogPostCategoryRepository.findById(id);
    }
    /**
     * @param id id of post to return
     * @return Optional of DevLogPostDto containing id, category, title, message, author, createdAt, lastModified, number of likes, replies, isUpdated
     */
    @Override
    public Optional<DevLogPostDto> getDevLogPostByIdDto(Long id) {
        log.trace("Attempting to get DevLogPostDto by ID: {}", id);
        return devLogPostRepository.findById(id).map(DevLogUtils::toDto);
    }
    /**
     * @param id id of post to return
     * @return Optional of DevLogPost entity
     */
    private Optional<DevLogPost> getDevLogPostById(Long id){
        log.trace("Attempting to get DevLogPost by ID: {}", id);
        return devLogPostRepository.findById(id);
    }
    /**
     * @param id id of reply to return
     * @return Optional of DevLogPostReply entity
     */
    private Optional<DevLogPostReply> getDevLogPostReplyById(Long id){
        log.trace("Attempting to get DevLogPostReply by ID: {}", id);
        return devLogPostReplyRepository.findById(id);
    }
    /**
     * Method to get DevLogPosts filtered by parameters, page request is built using helper method so page and size can be null
     * @param page nullable page number of results, will default if not set
     * @param size nullable size of page, will default if not set
     * @param category nullable, if set will filter results strictly by category
     * @param search nullable, if set will filter results by message, username and title containing query
     * @param startDate nullable, if set with end date will filter results between the two dates
     * @param endDate nullable, if set with start date will filter results between the two dates
     * @param isPopular nullable, if set will order results by number of likes
     * @param username nullable, if set will filter results strictly by username of author
     * @return Page of DevLogPostDto containing post Id, category, title, message, author, createdAt, lastModified, number of likes, list of replies and isUpdated
     * @throws NotFoundException if no posts matching search criteria are found
     */
    @Override
    public Page<DevLogPostDto> getDevLogPosts(Integer page, Integer size, String category, String search, LocalDateTime startDate, LocalDateTime endDate, Boolean isPopular, String username, Boolean active) {

        log.info("Attempting to get DevLogPosts by search criteria. \nCategory: {} \nSearch: {} \nStartDate: {} \nEndDate: {} \nIsPopularFlag: {} \nBy Username: {} \nIsActive: {}" , category, search, startDate, endDate, isPopular, username, active);

        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);
        Specification<DevLogPost> spec = Specification
                .where(DevLogPostSpecification.getPostsByCategory(category))
                .and(DevLogPostSpecification.getPostsBySearch(search))
                .and(DevLogPostSpecification.getPostBetweenDates(startDate, endDate))
                .and(DevLogPostSpecification.getPostByPopularity(isPopular, startDate, endDate))
                .and(DevLogPostSpecification.getPostsByUserUsername(username))
                .and(DevLogPostSpecification.getPostByActive(active));

        Page<DevLogPostDto> result = devLogPostRepository.findAll(spec, pageRequest).map(DevLogUtils::toDto);
        if(result.getContent().isEmpty()) {
            log.info("Search with criteria yielded no posts.");
            throw new NotFoundException("No posts found with search criteria provided.");
        }
        log.info("Retrieved all DevLogPosts: {}", result.getTotalElements());
        return result;
    }
    /**
     * Method to get DevLogPostsReplies for a given post filtered by parameters, page request is built using helper method so page and size can be null
     * @param page nullable, page number
     * @param size nullable, page size
     * @param postId the id of the post
     * @param search nullable, search query if set will filter results by content
     * @param startDate nullable, if set with endDate will filter results between the two dates
     * @param endDate nullable, if set with startDate will filter results between the two dates
     * @return Page of DevLogPostReplyDto filtered by parameters for post with Id provided, containing the reply ID, message, author, createdAt, lastModified, isUpdated and number of likes
     * @throws NotFoundException if no replies are found
     */
    @Override
    public Page<DevLogPostReplyDto> getPostReplies(Integer page, Integer size, Long postId, String search, LocalDateTime startDate, LocalDateTime endDate){
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);
        log.info("Attempting to find all replies for Post: {} with search criteria. \nSearch: {} \nStartDate: {} \nEndDate: {}", postId, search, startDate, endDate);

        if(getDevLogPostById(postId).isEmpty()) {
            log.warn("No Post found for ID: {}", postId);
            throw new NotFoundException("Post with ID: " + postId + " not found.");
        }

        log.info("Retrieving replies for Post: {}", postId);
        Specification<DevLogPostReply> spec = Specification
                .where(DevLogPostReplySpecification.bySearchLike(search))
                .and(DevLogPostReplySpecification.byDateRange(startDate, endDate))
                .and(DevLogPostReplySpecification.byPostId(postId));

        Page<DevLogPostReplyDto> result = devLogPostReplyRepository.findAll(spec,pageRequest).map(DevLogUtils::toDto);
        if(result.getContent().isEmpty()) {
            log.info("Search with criteria yielded no posts.");
            throw new NotFoundException("No replies found with search criteria provided.");
        }
        log.info("Retrieved all replies: {}", result.getTotalElements());
        return result;
    }
    /**
     * Method to get DevLogPostsReplies for all posts filtered by parameters, page request is built using helper method so page and size can be null
     * @param page nullable, page number
     * @param size nullable, page size
     * @param search nullable, if set will filter results by content
     * @param startDate nullable, if set with endDate will filter results between the two dates
     * @param endDate nullable, if set with startDate will filter results between the two dates
     * @return Page of DevLogPostReplyDto filtered by parameters for all posts, containing the reply ID, message, author, createdAt, lastModified, isUpdated and number of likes
     * @throws NotFoundException if no posts matching criteria are found
     */
    @Override
    public Page<DevLogPostReplyDto> getPostReplies(Integer page, Integer size, String search, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Attempting to find all replies with search criteria. \nSearch: {} \nStartDate: {} \nEndDate: {}", search, startDate, endDate);

        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);

        Specification<DevLogPostReply> spec = Specification
                .where(DevLogPostReplySpecification.bySearchLike(search))
                .and(DevLogPostReplySpecification.byDateRange(startDate, endDate));

        Page<DevLogPostReplyDto> result = devLogPostReplyRepository.findAll(spec,pageRequest).map(DevLogUtils::toDto);
        if(result.getContent().isEmpty()) {
            log.info("Search with criteria yielded no posts.");
            throw new NotFoundException("No replies found with search criteria provided.");
        }
        log.info("Retrieved all Replies: {}", result.getTotalElements());
        return result;
    }
    /**
     * @param id id of the reply searching for
     * @return Optional of a PostReplyDto containing the Author name, message, number of likes, created date, lastModified date
     * if the post has been updated and the ID of the reply
     */
    @Override
    public Optional<DevLogPostReplyDto> getPostReply(Long id) {
        log.trace("Attempting to find post reply Dto by ID: {}", id);
        return devLogPostReplyRepository.findById(id).map(DevLogUtils::toDto);
    }
    /**
     * @param postId id of the post the likes are attributed to
     * @param page page number
     * @param size page size
     * @return A page of size and number determined by parameters of Objects containing the username and time created of likes
     * on post as provided in the parameters
     */
    @Override
    public Page<DevLogPostLikeDto> getPostLikes(Long postId, Integer page, Integer size) {
        log.trace("Attempting to find likes for post with ID: {}", postId);
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);
        Page<DevLogPostLikeDto> likes = devLogPostLikeRepository.findAllByPostId(postId, pageRequest).map(DevLogUtils::toDto);

        if(likes.isEmpty()){
            log.trace("No likes found for post with ID: {}", postId);
            throw new NotFoundException("No likes found for post with ID: " + postId);
        }
        log.info("Retrieved all likes for post: {}", postId);
        return likes;
    }
    /**
     *
     * @param replyId id of the reply the likes are attributed to
     * @param page page number
     * @param size page size
     * @return A page of size and number determined by parameters of Objects containing the username and time created of likes
     * on reply as provided in the parameters
     */
    @Override
    public Page<DevLogPostLikeDto> getReplyLikes(Long replyId, Integer page, Integer size) {
        log.trace("Attempting to find likes for reply with ID: {}", replyId);
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);
        Page<DevLogPostLikeDto> likes = devLogPostReplyLikeRepository.findByReplyId(replyId, pageRequest).map(DevLogUtils::toDto);
        if(likes.isEmpty()){
            log.trace("No likes found for reply with ID: {}", replyId);
            throw new NotFoundException("No likes found for reply with ID: " + replyId);
        }
        log.info("Retrieved all likes for reply: {}", replyId);
        return likes;
    }
    
    public Optional<DevLogPostDto> getLatestPost(){
        return devLogPostRepository.findFirstByOrderByCreatedAtDesc().map(DevLogUtils::toDto);
    }
    
    
    //Update
    
    /**
     * @param update DevLogPostPatchDto containing optional fields of CategoryId, message, title, active. If field is null update for that part is ignored.
     * @param postId id of post to update
     * @return null if none of the values are updated, or an updated DevLogPostDto with the ID, category, title, message, author, createdAt, lastModified,
     * number of likes, list of replies and if is updated (which will be true after this method)
     */
    @Override
    public DevLogPostDto updateDevLogPost(DevLogPostPatchDto update, Long postId) {
        log.trace("Attempting to update post with ID: {}", postId);
        if(getDevLogPostById(postId).isEmpty()) {
            log.warn("No Post found for ID: {}", postId);
            throw new NotFoundException("Post with ID: " + postId + " not found.");
        }
        DevLogPost post = getDevLogPostById(postId).get();
        boolean isUpdated = false;

        if(update.getCategoryId() != null) {
            Optional<DevLogPostCategory> newCategory = getDevLogPostCategoryById(update.getCategoryId());
            if(newCategory.isEmpty()) {
                log.warn("No Category found for ID: {}", update.getCategoryId());
                throw new BadRequestException("Category with ID: " + update.getCategoryId() + " not found.");
            }
            else{
                log.info("Updating post with ID: {} with Category: {}", postId , newCategory.get().getCategory());
                post.setDevLogPostCategory(newCategory.get());
                isUpdated = true;
            }
        }
        if(update.getMessage()!= null){
            log.info("Updating post with ID: {} with Message: {}", postId , update.getMessage());
            post.setMessage(update.getMessage());
            isUpdated = true;
        }
        if(update.getTitle()!= null){
            log.info("Updating post with ID: {} with Title: {}", postId , update.getTitle());
            post.setTitle(update.getTitle());
            isUpdated = true;
        }
        if(update.getActive()!= null){
            log.info("Updating post with ID: {} with Active: {}", postId , update.getActive());
            post.setActive(update.getActive());
            isUpdated = true;
        }
        if(isUpdated){
            try {
                log.info("Attempting to save update for post with ID: {}", postId);
                post.setUpdated(true);
                post.setLastModified(LocalDateTime.now());
                devLogPostRepository.save(post);
                log.info("Post with ID: {} updated successfully, last modified set to: {}.", postId, LocalDateTime.now());
                return DevLogUtils.toDto(post);
            }
            catch (Exception e) {
                log.error("Unable to save update for post with ID: {} with error: {}", postId, e.getMessage());
                throw new BadRequestException("Unable to save update for post with ID: " + postId);
            }
        }
        else{
            log.info("No update to be made for post with ID: {}", postId);
            return null;
        }
    }
    /**
     * @param update DevLogPostCategoryPatchDto containing new category name
     * @param categoryId id of category to be renamed
     * @return null if no update, DevLogPostCategoryDto containing category name and Id of updated category.
     */
    @Override
    public DevLogPostCategoryDto updateDevLogPostCategory(DevLogPostCategoryPatchDto update, Long categoryId) {
        log.trace("Attempting to update category with ID: {}", categoryId);
        if(getDevLogPostCategoryById(categoryId).isEmpty()) {
            log.warn("No Category found for ID: {}", categoryId);
            throw new BadRequestException("Category with ID: " + categoryId + " not found.");
        }
        DevLogPostCategory category = getDevLogPostCategoryById(categoryId).get();

        if(update.getCategory() != null) {
            try {
                log.info("Attempting to save update for category with ID: {}", categoryId);
                category.setCategory(update.getCategory());
                category.setLastModified(LocalDateTime.now());
                category = devLogPostCategoryRepository.save(category);
                log.info("Category with ID: {} saved successfully.", categoryId);
                return DevLogUtils.toDto(category);
            }
            catch(Exception e){
                log.error("Unable to save update for category with ID: {} with error: {}", categoryId, e.getMessage());
                throw new BadRequestException("Unable to save update for category with ID: " + categoryId);
            }
        }
        else{
            log.info("No update to be made for Category: {}", categoryId);
            return null;
        }
    }
    /**
     * @param update DevLogPostReplyPatchDto containing optional field of message
     * @param postId id of post parent to the reply
     * @param replyId id of reply to be updated
     * @return null if update is empty, DevLogPostReplyDto object containing replyId, message, author, createdAt, lastModified, isUpdated and number of likes
     */
    @Override
    public DevLogPostReplyDto updateDevLogPostReply(DevLogPostReplyPatchDto update, Long postId, Long replyId) {
        if(getDevLogPostReplyById(replyId).isEmpty()) {
            log.warn("No Reply found for ID: {}", replyId);
            throw new BadRequestException("Reply with ID: " + postId + " not found.");
        }
        if(getDevLogPostById(postId).isEmpty()) {
            log.warn("No Post found for ID: {}", postId);
            throw new BadRequestException("Post with ID: " + postId + " not found.");
        }
        DevLogPostReply reply = getDevLogPostReplyById(replyId).get();
        if(update.getMessage()!= null){
            try {
                log.info("Attempting to save update for reply with ID: {}", replyId);
                reply.setMessage(update.getMessage());
                reply.setUpdated(true);
                reply.setLastModified(LocalDateTime.now());
                reply = devLogPostReplyRepository.save(reply);
                log.info("Reply with ID: {} updated successfully.", replyId);
                return DevLogUtils.toDto(reply);
            }
            catch (Exception e) {
                log.error("Unable to save reply with ID: {} with error: {}", replyId, e.getMessage());
                throw new BadRequestException("Unable to save reply with ID: " + replyId);
            }
        }
        else {
            log.info("No update to be made for reply with ID: {}", replyId);
            return null;
        }
    }
    /**
     * @param userId UUID of user
     * @param postId id of post to be liked
     * @return null if like already exists as like is removed, DevLogPostLikeDto object containing username and createdAt timestamp
     */
    @Override
    public DevLogPostLikeDto toggleDevLogPostLike(UUID userId, Long postId) {

        log.trace("Attempting to update like status for post with ID: {} ", postId);

        if(userId == null){
            log.info("No user provided to toggle like status for post with ID: {}", postId);
            throw new BadRequestException("User id is required to toggle like status.");
        }

        if(userService.getUserById(userId).isEmpty()) {
            log.warn("No User found for ID: {}", userId);
            throw new BadRequestException("Unable to like as user information unable to be retrieved");
        }

        if(getDevLogPostById(postId).isEmpty()) {
            log.warn("No Post found for ID: {}", postId);
            throw new BadRequestException("Unable to like as post information unable to be retrieved");
        }

        DevLogPostLike like = new DevLogPostLike();
        Optional<DevLogPostLike> likeCheck = devLogPostLikeRepository.findByPostIdAndUserId(postId,userId);
        if(likeCheck.isPresent()) {
            try {
                log.info("Attempting to delete like for post with ID: {} for user with ID: {}", postId, userId);
                devLogPostLikeRepository.delete(likeCheck.get());
                log.info("Like successfully deleted for post with ID: {} for user with ID: {}", postId, userId);
                return null;
            }
            catch(Exception e){
                log.error("Unable to delete like for post with ID: {} for user with ID: {} with error: {}", postId, userId, e.getMessage());
                throw new BadRequestException("Unable to delete like for post with ID: " + postId);
            }
        }
        else{
            try {
                log.info("Attempting to save like for post with ID: {} for user with ID: {}", postId, userId);
                like.setPost(getDevLogPostById(postId).get());
                like.setUser(userService.getUserById(userId).get());
                like.setCreatedAt(LocalDateTime.now());
                like.setLastModified(LocalDateTime.now());
                like.setActive(true);
                like = devLogPostLikeRepository.save(like);
                log.info("Like successfully added for post with ID: {} for user with ID: {}", postId, userId);
                return DevLogUtils.toDto(like);
            }
            catch(Exception e){
                log.error("Unable to save like for post with ID: {} for user with ID: {} with error: {}", postId, userId, e.getMessage());
                throw new BadRequestException("Unable to save like for post with ID: " + postId);
            }
        }
    }
    /**
     * @param userId UUID of current logged in user
     * @param postId id of parent post for reply
     * @param replyId id of reply to be liked
     * @return null if like already exists as like is removed, DevLogPostLikeDto object containing username and createdAt timestamp
     */
    @Override
    public DevLogPostLikeDto toggleDevLogPostReplyLike(UUID userId, Long postId, Long replyId) {

        log.trace("Attempting to update like status for reply with ID: {} ", postId);

        if(userId == null){
            log.warn("No user provided to toggle like status for reply with ID: {}", postId);
            throw new BadRequestException("User id is required to toggle like status.");
        }
        if(userService.getUserById(userId).isEmpty()) {
            log.warn("No User found for ID: {}", userId);
            throw new BadRequestException("Unable to like as user information unable to be retrieved");
        }
        if(getDevLogPostById(postId).isEmpty()) {
            log.warn("No Post found for ID: {}", postId);
            throw new BadRequestException("Unable to like as post information unable to be retrieved");
        }
        if(getDevLogPostReplyById(replyId).isEmpty()) {
            log.warn("No Reply found for ID: {}", replyId);
            throw new BadRequestException("Unable to like as reply information unable to be retrieved");
        }

        Optional<DevLogPostReplyLike> likeCheck = devLogPostReplyLikeRepository.findByUserIdAndReplyId(userId, replyId);
        if(likeCheck.isPresent()) {
            try {
                log.info("Attempting to delete like for reply with ID: {} for user with ID: {}", replyId, userId);
                devLogPostReplyLikeRepository.delete(likeCheck.get());
                log.info("Like successfully added for reply with ID: {} for user with ID: {}", replyId, userId);
                return null;
            }
            catch(Exception e){
                log.error("Unable to delete like for reply with ID: {}", replyId);
                throw new BadRequestException("Unable to delete like for reply with ID: " + replyId);
            }
        }
        else {
            try {
                log.info("Attempting to create like for reply with ID: {} for user with ID: {}", replyId, userId);
                DevLogPostReplyLike like = new DevLogPostReplyLike();
                like.setActive(true);
                like.setCreated(LocalDateTime.now());
                like.setLastModified(LocalDateTime.now());
                like.setUser(userService.getUserById(userId).get());
                like.setReply(getDevLogPostReplyById(replyId).get());
                like = devLogPostReplyLikeRepository.save(like);
                log.info("Like successfully added for reply with ID: {} for user with ID: {}", replyId, userId);
                return DevLogUtils.toDto(like);
            }
            catch(Exception e){
                log.error("Unable to save like for reply with ID: {} for user with ID: {}", replyId, userId);
                throw new BadRequestException("Unable to savd like for reply with ID: " + replyId);
            }
        }
    }

    //Delete Methods

    /**
     * @param categoryId id of category to be deleted
     * @throws NotFoundException if category with id provided cannot be located
     * @throws BadRequestException if category is unable to be deleted from the database
     */
    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("Attempting to delete category with ID: {} ", categoryId);
        if(getDevLogPostCategoryById(categoryId).isEmpty()) {
            log.warn("Category with ID: {} not found", categoryId);
            throw new NotFoundException("Category with ID: " + categoryId + " not found.");
        }
        else{
            DevLogPostCategory category = getDevLogPostCategoryById(categoryId).get();
            List<DevLogPost> posts = devLogPostRepository.findAllByDevLogPostCategoryId(categoryId);
            if(!posts.isEmpty()) {
                log.info("Category has posts, proceeding to delete all posts for category with ID: {}", categoryId);
                for(DevLogPost post : posts) {
                    deletePost(post.getId());
                }
            }
            try {
                log.info("Attempting to delete category with ID: {} from the database", categoryId);
                devLogPostCategoryRepository.delete(category);
                log.info("Category with ID: {} successfully deleted from the database", categoryId);
            }
            catch(Exception e){
                log.error("Unable to delete category with ID: {}", categoryId);
                throw new BadRequestException("Unable to delete category with ID: " + categoryId);
            }
        }
    }
    /**
     * @param postId id of post to be deleted
     * @throws NotFoundException if post with ID provided in parameters can not be found
     * @throws BadRequestException if post is unable to be saved to the database
     */
    @Override
    @Transactional
    public void deletePost(Long postId) {
        log.info("Attempting to delete post with ID: {} ", postId);
        Optional<DevLogPost> postCheck = devLogPostRepository.findById(postId);
        if(postCheck.isPresent()) {
            DevLogPost post = postCheck.get();
            if(!post.getLikes().isEmpty()) {
                log.info("Post has likes, proceeding to delete all likes for post with ID: {}", postId);
                try {
                    log.info("Attempting to delete likes from database for post with ID: {}", postId);
                    devLogPostLikeRepository.deleteAll(post.getLikes());
                    log.info("Like successfully deleted for database for post with ID: {}", postId);
                }
                catch(Exception e){
                    log.error("unable to delete likes from database for post with ID: {}", postId);
                }
            }
            if(!post.getReplies().isEmpty()) {
                log.info("Post has replies, proceeding to delete all replies for post with ID: {}", postId);
                for(DevLogPostReply reply : post.getReplies()) {
                    deleteReply(reply.getId());
                }
            }
            try {
                log.info("Attempting to delete post from database for post with ID: {}", postId);
                devLogPostRepository.delete(post);
                log.info("Post with ID: {} successfully deleted from the database", postId);
            }
            catch(Exception e){
                log.error("Unable to delete post with ID: {} from the database", postId);
                throw new BadRequestException("Unable to delete post with ID: " + postId);
            }
        }
        else {
            log.warn("Post with ID: {} not found.", postId);
            throw new NotFoundException("Post with ID: " + postId + " not found.");
        }
    }
    /**
     * @param replyId - Id of reply to be deleted
     * @throws NotFoundException if reply cannot be found by ID provided
     * @throws BadRequestException if reply cannot be deleted from the database
     */
    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        log.info("Attempting to delete reply with ID: {} ", replyId);
        Optional<DevLogPostReply> replyCheck = devLogPostReplyRepository.findById(replyId);
        if(replyCheck.isPresent()) {
            if(!replyCheck.get().getLikes().isEmpty()){
                log.info("Reply has likes, proceeding to delete all likes for reply with ID: {}", replyId);
                try {
                    log.info("Attempting to delete likes from database for reply with ID: {}", replyId);
                    devLogPostReplyLikeRepository.deleteAll(replyCheck.get().getLikes());
                    log.info("Likes for reply with ID: {} successfully deleted from the database", replyId);
                }
                catch(Exception e){
                    log.error("unable to delete likes from database for reply with ID: {}", replyId);
                }
            }
            try {
                log.info("Attempting to delete reply from database for reply with ID: {}", replyId);
                devLogPostReplyRepository.delete(replyCheck.get());
                log.info("Reply with ID: {} successfully deleted from the database", replyId);
            }
            catch(Exception e){
                log.error("Unable to delete reply with ID: {} from the database", replyId);
                throw new BadRequestException("Unable to delete reply with ID: " + replyId);
            }
        }
        else{
            log.warn("Reply with ID: {} not found.", replyId);
            throw new NotFoundException("Reply with ID: " + replyId + " not found.");
        }
    }
}
