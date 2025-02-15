package com.carnasa.cr.projectkingdomwebpage.services.interfaces;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPost;
import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostReply;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostPatchDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.DevLogPostPostDto;
import org.springframework.stereotype.Service;

@Service
public interface DevLogPostService {

    //create

    DevLogPost createDevLogPost(DevLogPostPostDto devLogPostPostDto);
    DevLogPostReply createDevLogPostReply(DevLogPostReply devLogPostReply);

    //Read

    //Update
    DevLogPostDto updateDevLogPost(DevLogPostPatchDto update);

}
