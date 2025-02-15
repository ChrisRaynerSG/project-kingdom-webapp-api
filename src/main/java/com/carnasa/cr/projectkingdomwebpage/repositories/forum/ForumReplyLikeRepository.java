package com.carnasa.cr.projectkingdomwebpage.repositories.forum;

import com.carnasa.cr.projectkingdomwebpage.entities.forum.ForumReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumReplyLikeRepository extends JpaRepository<ForumReplyLike, Long> {
}
