package com.carnasa.cr.projectkingdomwebpage.repositories.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReplyLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DevLogPostReplyLikeRepository extends JpaRepository<DevLogPostReplyLike, Long> {
    Optional<DevLogPostReplyLike> findByUserIdAndReplyId(UUID userId, Long replyId);
    Page<DevLogPostReplyLike> findByReplyId(Long replyId, Pageable pageable);
}
