package com.carnasa.cr.projectkingdomwebpage.repositories.specifications;

import com.carnasa.cr.projectkingdomwebpage.entities.forum.ForumPost;
import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ForumPostSpecification {

    public static Specification<ForumPost> getPostByTitle(String title) {
        if(title == null){
            return null;
        }
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("active"), "%" + title + "%");
    }

    public static Specification<ForumPost> getPostByCategory(String category) {
        if(category == null){
            return null;
        }
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category);
    }
    public static Specification<ForumPost> getPostByUserEntityUsername(String username) {
        if(username == null){
            return null;
        }
        return (root, criteriaQuery, criteriaBuilder) ->
        {
            root.fetch("creator", JoinType.LEFT);
            return criteriaBuilder.equal(root.get("creator").get("username"), username);
        };
    }
    public static Specification<ForumPost> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate == null || endDate == null){
            return null;
        }
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be after end date"); // maybe change to custom exception
        }

        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate),
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate)
        );
    }
}
