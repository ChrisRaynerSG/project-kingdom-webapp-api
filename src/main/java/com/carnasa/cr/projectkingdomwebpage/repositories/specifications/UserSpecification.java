package com.carnasa.cr.projectkingdomwebpage.repositories.specifications;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UserEntity> getUserByUsername(String username) {
        if(username == null) {
            return null;
        }
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("username"), "%" + username + "%");
    }

    public static Specification<UserEntity> getUserByActive(Boolean active) {
        if(active == null){
            return null;
        }
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("active"), active);
    }

}
