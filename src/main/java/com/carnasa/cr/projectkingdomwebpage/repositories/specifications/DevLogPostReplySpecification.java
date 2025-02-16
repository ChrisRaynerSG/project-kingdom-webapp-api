package com.carnasa.cr.projectkingdomwebpage.repositories.specifications;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.BadRequestException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class DevLogPostReplySpecification {

    public static Specification<DevLogPostReply> bySearchLike(String search) {
        if(search == null){
            return null;
        }
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<DevLogPostReply, UserEntity> creatorJoin = root.join("user", JoinType.LEFT);
            Predicate messagePredicate = criteriaBuilder.like(root.get("message"), "%" + search + "%");
            Predicate userPredicate = criteriaBuilder.like(criteriaBuilder.lower(creatorJoin.get("username")), "%" + search + "%");
            return criteriaBuilder.or(messagePredicate, userPredicate);
        };
    }
    public static Specification<DevLogPostReply> byDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate == null || endDate == null) {
            return null;
        }
        else if(startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
            criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate),
            criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate)
        );
    }
    public static Specification<DevLogPostReply> byPostId(Long postId) {
        if(postId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("post").get("id"), postId);
    }
}
