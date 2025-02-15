package com.carnasa.cr.projectkingdomwebpage.controllers;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserPatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.UserUtils;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    public static final String URL = "/api/v1/project-kingdom/users";
    public static final String URL_ID = URL + "/{id}";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(URL)
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestParam(required =false) Integer pageSize,
                                                     @RequestParam(required= false) Integer pageNumber,
                                                     @RequestParam(required = false) String usernameContains,
                                                     @RequestParam(required = false) Boolean isActive) {
        return new ResponseEntity(userService.getAllUsers(pageSize,pageNumber, usernameContains, isActive).getContent(),HttpStatus.OK);
    }

    @GetMapping(URL_ID)
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
        return userService.getUserDtoById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(URL)
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserEntity userEntity, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else{
            UserDto user = UserUtils.toDto(userService.saveUser(userEntity));
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }
    }

    @PatchMapping(URL_ID)
    public ResponseEntity<UserEntity> updateUser(@PathVariable("id") UUID id, @RequestBody UserPatchDto userPatchDto) {
        UserEntity user = userService.updateUser(userPatchDto, id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(URL_ID)
    public ResponseEntity<UserDto> deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
