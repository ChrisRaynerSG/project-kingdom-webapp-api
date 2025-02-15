package com.carnasa.cr.projectkingdomwebpage.repositories.devlog;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import org.springframework.stereotype.Repository;

@Repository
public interface DevLogPostReplyRepository extends JpaRepository<DevLogPostReply, Long> {
}
