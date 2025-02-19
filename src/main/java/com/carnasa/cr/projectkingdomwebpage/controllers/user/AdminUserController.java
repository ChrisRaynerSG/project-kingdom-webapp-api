package com.carnasa.cr.projectkingdomwebpage.controllers.user;

import com.carnasa.cr.projectkingdomwebpage.exceptions.status.InternalServerErrorException;
import com.carnasa.cr.projectkingdomwebpage.models.user.read.UserDetailDtoAdmin;
import com.carnasa.cr.projectkingdomwebpage.models.user.read.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.update.UserRolePatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.carnasa.cr.projectkingdomwebpage.utils.UrlUtils.*;
import static com.carnasa.cr.projectkingdomwebpage.utils.LoggingUtils.GET_ENDPOINT_LOG_HIT;

@RestController
public class AdminUserController {

    public static Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(ADMIN_USERS_URL)
    public ResponseEntity<List<UserDetailDtoAdmin>> getAllUsers(@RequestParam(required =false) Integer pageSize,
                                                                @RequestParam(required= false) Integer pageNumber,
                                                                @RequestParam(required = false) String usernameContains,
                                                                @RequestParam(required = false) Boolean isActive) {
        log.trace(GET_ENDPOINT_LOG_HIT,ADMIN_USERS_URL);
        return new ResponseEntity<>(userService.getAllUsers(pageSize,pageNumber,usernameContains,isActive).map(UserUtils::toAdminDto).getContent(), HttpStatus.OK);
    }

    @PatchMapping(ADMIN_USERS_URL_ID)
    public ResponseEntity<UserDto> updateUserRoles(@PathVariable UUID userId, @RequestBody UserRolePatchDto update){
        log.trace(GET_ENDPOINT_LOG_HIT,ADMIN_USERS_URL_ID);
        try {
            UserDto user = userService.patchUserRoles(userId, update);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch(Exception e){
            log.error("Error: {} while updating user roles", e.getMessage());
            throw new InternalServerErrorException("Error while updating user roles. Error: " + e.getMessage());
        }
    }
}
