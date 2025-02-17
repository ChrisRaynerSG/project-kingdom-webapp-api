package com.carnasa.cr.projectkingdomwebpage.controllers.user;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserPatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import static com.carnasa.cr.projectkingdomwebpage.utils.LoggingUtils.*;


@RestController
public class UserController {

    public static Logger log = LoggerFactory.getLogger(UserController.class);

    public static final String USER_URI = "/api/v1/project-kingdom/users";
    public static final String USER_URI_ID = USER_URI + "/{id}";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        log.trace("Creating user controller");
        this.userService = userService;
    }

    @GetMapping(USER_URI)
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(required =false) Integer pageSize,
                                                     @RequestParam(required= false) Integer pageNumber,
                                                     @RequestParam(required = false) String usernameContains,
                                                     @RequestParam(required = false) Boolean isActive) {
        log.trace(GET_ENDPOINT_LOG_HIT,USER_URI);
        return new ResponseEntity<>(userService.getAllUsers(pageSize,pageNumber, usernameContains, isActive).getContent(),HttpStatus.OK);
    }

    @GetMapping(USER_URI_ID)
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
        log.trace(GET_ENDPOINT_LOG_HIT,USER_URI_ID);
        if(userService.getUserDtoById(id).isEmpty()){
            throw new NotFoundException("User with id " + id + " not found");
        }
        else{
            return new ResponseEntity<>(userService.getUserDtoById(id).get(), HttpStatus.OK);
        }
    }

    @PatchMapping(USER_URI_ID)
    public ResponseEntity<UserEntity> updateUser(@PathVariable("id") UUID id, @RequestBody UserPatchDto userPatchDto) {
        log.trace(PATCH_ENDPOINT_LOG_HIT,USER_URI_ID);
        UserEntity user = userService.updateUser(userPatchDto, id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(USER_URI_ID)
    public ResponseEntity<UserDto> deleteUser(@PathVariable("id") UUID id) {
        log.trace(DELETE_ENDPOINT_LOG_HIT,USER_URI_ID);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
