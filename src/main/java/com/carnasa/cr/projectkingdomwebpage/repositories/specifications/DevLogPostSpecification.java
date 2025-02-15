package com.carnasa.cr.projectkingdomwebpage.repositories.specifications;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostLike;
import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.exceptions.BadRequestException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class DevLogPostSpecification {

    public static Specification<DevLogPost> getPostsBySearch(String searchQuery) {
        if(searchQuery == null) {
            return null;
        }
        return (root, query, cb) ->
        {
            Join<DevLogPost, UserEntity> creatorJoin = root.join("creator", JoinType.LEFT);
            Predicate titlePredicate = cb.like(cb.lower(root.get("title")), "%" + searchQuery.toLowerCase() + "%");
            Predicate contentPredicate = cb.like(cb.lower(root.get("message")), "%" + searchQuery.toLowerCase() + "%");
            Predicate usernamePredicate = cb.like(cb.lower(creatorJoin.get("username")), "%" + searchQuery.toLowerCase() + "%");

            return cb.or(titlePredicate, contentPredicate, usernamePredicate);
        };
    }

    public static Specification<DevLogPost> getPostsByCategory(String category) {
        if(category == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            root.join("devLogPostCategory", JoinType.LEFT);
            return criteriaBuilder.equal(root.get("devLogPostCategory").get("category"), category);
        };
    }

    public static Specification<DevLogPost> getPostsByUserUsername(String username) {
        if(username != null) {
            return(root, query, criteriaBuilder) -> {
                root.join("creator", JoinType.LEFT);
                return criteriaBuilder.equal(root.get("creator").get("username"), username);
            };
        }
        return null;
    }
    public static Specification<DevLogPost> getPostBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate == null || endDate == null) {
            return null;
        }
        if(startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date.");
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate),
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate)
        );
    }
    public static Specification<DevLogPost> getPostByPopularity(Boolean isPopular, LocalDateTime startDate, LocalDateTime endDate) {

        if(isPopular == null || !isPopular) {
            return null;
        }
        if (startDate == null || endDate == null) {
            return null;
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date.");
        }

        return (root, query, criteriaBuilder) -> {
            Join<DevLogPost, DevLogPostLike> likesJoin = root.join("likes", JoinType.LEFT);

            Predicate likesWithinPeriod = criteriaBuilder.between(likesJoin.get("createdAt"), startDate, endDate);

            Expression<Long> likesCount = criteriaBuilder.count(likesJoin);

            query.groupBy(root.get("id"));
            query.orderBy(criteriaBuilder.desc(likesCount));

            return likesWithinPeriod;
        };
    }
}
