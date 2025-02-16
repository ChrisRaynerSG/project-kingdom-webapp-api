package com.carnasa.cr.projectkingdomwebpage.repositories.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DevLogPostLikeRepository extends JpaRepository<DevLogPostLike, Long> {
    Optional<DevLogPostLike> findByPostIdAndUserId(Long postId, UUID userId);
    Page<DevLogPostLike> findAllByPostId(Long postId, Pageable pageable);
}
