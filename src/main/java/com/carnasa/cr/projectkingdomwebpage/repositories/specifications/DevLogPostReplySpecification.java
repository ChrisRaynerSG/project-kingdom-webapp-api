package com.carnasa.cr.projectkingdomwebpage.repositories.specifications;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import org.springframework.data.jpa.domain.Specification;

public class DevLogPostReplySpecification {

    public static Specification<DevLogPostReply> byPostIdEqualTo(Long postId) {
        if(postId == null){
            return null;
        }
        //@todo make this work
        return null;
    }
}
