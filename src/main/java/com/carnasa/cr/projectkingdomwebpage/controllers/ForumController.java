package com.carnasa.cr.projectkingdomwebpage.controllers;

import com.carnasa.cr.projectkingdomwebpage.services.interfaces.ForumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static com.carnasa.cr.projectkingdomwebpage.utils.LoggingUtils.*;

@RestController
public class ForumController {

    public Logger log = LoggerFactory.getLogger(ForumController.class);

    private final ForumService forumService;

    @Autowired
    public ForumController(ForumService forumService) {
        log.trace("Creating ForumController");
        this.forumService = forumService;
    }
}
