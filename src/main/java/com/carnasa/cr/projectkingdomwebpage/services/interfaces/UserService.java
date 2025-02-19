package com.carnasa.cr.projectkingdomwebpage.services.interfaces;

import com.carnasa.cr.projectkingdomwebpage.entities.user.*;
import com.carnasa.cr.projectkingdomwebpage.models.user.read.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.update.UserPatchDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.create.UserPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.update.UserRolePatchDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {

    //Read Methods
    Optional<UserEntity> getUserById(UUID userId);
    Optional<UserDto> getUserDtoById(UUID userId);
    Optional<UserEntity> getByUsername(String username);

    Page<UserEntity> getAllUsers(Integer pageSize,
                                 Integer page,
                                 String username,
                                 Boolean active);

    //Create Methods
    UserEntity saveUser(UserPostDto user);

    //Update Methods
    UserEntity updateUser(UserPatchDto userPatchDto, UUID userId);
    UserDto patchUserRoles(UUID userId, UserRolePatchDto userPatchDto);
    void userLoggedIn(String username);

    //Delete Methods
    void deleteUser(UUID userId);
}
