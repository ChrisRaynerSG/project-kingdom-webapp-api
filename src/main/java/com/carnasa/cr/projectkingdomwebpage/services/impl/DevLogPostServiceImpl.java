package com.carnasa.cr.projectkingdomwebpage.services.impl;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostCategory;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevlogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevlogPostReplyDto;
import com.carnasa.cr.projectkingdomwebpage.repositories.devlog.*;
import com.carnasa.cr.projectkingdomwebpage.repositories.specifications.DevLogPostSpecification;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevlogUtils;
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

@Service
public class DevLogPostServiceImpl implements DevLogPostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevLogPostServiceImpl.class);

    private final DevLogPostRepository devLogPostRepository;
    private final DevLogPostCategoryRepository devLogPostCategoryRepository;
    private final DevLogPostLikeRepository devLogPostLikeRepository;
    private final DevLogPostReplyRepository devLogPostReplyRepository;
    private final DevLogPostReplyLikeRepository devLogPostReplyLikeRepository;

    @Autowired
    public DevLogPostServiceImpl(DevLogPostRepository devLogPostRepository,
                                 DevLogPostCategoryRepository devLogPostCategoryRepository,
                                 DevLogPostLikeRepository devLogPostLikeRepository,
                                 DevLogPostReplyRepository devLogPostReplyRepository,
                                 DevLogPostReplyLikeRepository devLogPostReplyLikeRepository) {

        this.devLogPostRepository = devLogPostRepository;
        this.devLogPostCategoryRepository = devLogPostCategoryRepository;
        this.devLogPostLikeRepository = devLogPostLikeRepository;
        this.devLogPostReplyRepository = devLogPostReplyRepository;
        this.devLogPostReplyLikeRepository = devLogPostReplyLikeRepository;
    }

    //Create Methods


    //Read Methods

    /**
     * Method to populate drop down list of all categories
     */
    public List<DevLogPostCategory> getDevLogPostCategories() {
        return devLogPostCategoryRepository.findAll();
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
    public Page<DevlogPostDto> getDevLogPosts(Integer page, Integer size, String category, String search, LocalDateTime startDate, LocalDateTime endDate, Boolean isPopular, String username) {
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);
        Specification<DevLogPost> spec = Specification
                .where(DevLogPostSpecification.getPostsByCategory(category))
                .and(DevLogPostSpecification.getPostsBySearch(search))
                .and(DevLogPostSpecification.getPostBetweenDates(startDate, endDate))
                .and(DevLogPostSpecification.getPostByPopularity(isPopular))
                .and(DevLogPostSpecification.getPostsByUserUsername(username));

        return devLogPostRepository.findAll(spec, pageRequest).map(DevlogUtils::toDto);
    }

    /**
     *
     * @param page
     * @param size
     * @param postId the id of the post
     * @param username username if searching a users replies
     * @return
     */

    public Page<DevlogPostReplyDto> getDevLogPostReplies(Integer page, Integer size, Long postId, String username){
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, size);

        //@todo add spec for filtering results

        devLogPostReplyRepository.findAll(pageRequest);
    }

    //Update Methods

    //Delete Methods
}
