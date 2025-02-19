package com.carnasa.cr.projectkingdomwebpage.controllers.user;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.user.read.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.read.UserPersonalDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.update.UserPatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.SecurityUtils;
import com.carnasa.cr.projectkingdomwebpage.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import static com.carnasa.cr.projectkingdomwebpage.utils.LoggingUtils.*;
import static com.carnasa.cr.projectkingdomwebpage.utils.UrlUtils.*;

@RestController
public class UserController {

    public static Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        log.info("Creating user controller");
        this.userService = userService;
    }

    @GetMapping(USER_URI_ID)
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
        log.info(GET_ENDPOINT_LOG_HIT,USER_URI_ID);
        if(userService.getUserDtoById(id).isEmpty()){
            throw new NotFoundException("User with id " + id + " not found");
        }
        else{
            return new ResponseEntity<>(userService.getUserDtoById(id).get(), HttpStatus.OK);
        }
    }

    @GetMapping(USER_URI_ME)
    public ResponseEntity<UserPersonalDto> getSignedInUser(){
        log.info(GET_ENDPOINT_LOG_HIT,USER_URI_ME);
        try {
            SecurityUtils.getCurrentUserId();
            UserEntity user = userService.
                    getUserById(SecurityUtils.getCurrentUserId())
                    .orElseThrow(() -> new NotFoundException("User with id " + SecurityUtils.getCurrentUserId() + " not found"));
            return new ResponseEntity<>(UserUtils.toPersonalDto(user), HttpStatus.OK);
        }
        catch (Exception e){
            log.info("User is not signed in to the system. Error: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PatchMapping(USER_URI_ID)
    public ResponseEntity<UserEntity> updateUser(@PathVariable("id") UUID id, @RequestBody UserPatchDto userPatchDto) {
        log.info(PATCH_ENDPOINT_LOG_HIT,USER_URI_ID);
        UserEntity user = userService.updateUser(userPatchDto, id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(USER_URI_ID)
    public ResponseEntity<UserDto> deleteUser(@PathVariable("id") UUID id) {
        log.info(DELETE_ENDPOINT_LOG_HIT,USER_URI_ID);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
