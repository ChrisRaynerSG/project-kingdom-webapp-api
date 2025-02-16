package com.carnasa.cr.projectkingdomwebpage.repositories.devlog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DevLogPostReplyRepository extends JpaRepository<DevLogPostReply, Long>, JpaSpecificationExecutor<DevLogPostReply> {
//    Page<DevLogPostReply> findAllByPostId(Long postId, Pageable pageable);
}
