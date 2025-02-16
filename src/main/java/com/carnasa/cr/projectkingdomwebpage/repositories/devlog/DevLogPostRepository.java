package com.carnasa.cr.projectkingdomwebpage.repositories.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevLogPostRepository extends JpaRepository<DevLogPost, Long>, JpaSpecificationExecutor<DevLogPost> {
    List<DevLogPost> findAllByDevLogPostCategoryId(Long categoryId);

}
