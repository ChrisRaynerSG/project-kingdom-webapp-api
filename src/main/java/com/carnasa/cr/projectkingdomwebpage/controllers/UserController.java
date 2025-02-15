package com.carnasa.cr.projectkingdomwebpage.controllers;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserPatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    public static final String USER_URI = "/api/v1/project-kingdom/users";
    public static final String USER_URI_ID = USER_URI + "/{id}";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(USER_URI)
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(required =false) Integer pageSize,
                                                     @RequestParam(required= false) Integer pageNumber,
                                                     @RequestParam(required = false) String usernameContains,
                                                     @RequestParam(required = false) Boolean isActive) {
        return new ResponseEntity(userService.getAllUsers(pageSize,pageNumber, usernameContains, isActive).getContent(),HttpStatus.OK);
    }

    @GetMapping(USER_URI_ID)
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
        return userService.getUserDtoById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(USER_URI)
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserEntity userEntity, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else{
            UserDto user = UserUtils.toDto(userService.saveUser(userEntity));
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
    }

    @PatchMapping(USER_URI_ID)
    public ResponseEntity<UserEntity> updateUser(@PathVariable("id") UUID id, @RequestBody UserPatchDto userPatchDto) {
        UserEntity user = userService.updateUser(userPatchDto, id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(USER_URI_ID)
    public ResponseEntity<UserDto> deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
