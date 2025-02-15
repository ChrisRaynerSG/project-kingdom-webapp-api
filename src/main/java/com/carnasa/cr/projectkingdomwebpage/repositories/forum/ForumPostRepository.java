package com.carnasa.cr.projectkingdomwebpage.repositories.forum;

import com.carnasa.cr.projectkingdomwebpage.entities.forum.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
}
