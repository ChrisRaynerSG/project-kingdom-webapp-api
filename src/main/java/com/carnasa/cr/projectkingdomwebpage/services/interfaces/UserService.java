package com.carnasa.cr.projectkingdomwebpage.services.interfaces;

import com.carnasa.cr.projectkingdomwebpage.entities.user.*;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserPatchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {

    //Read Methods
    Optional<UserEntity> getUserById(UUID userId);
    Optional<UserDto> getUserDtoById(UUID userId);

    Page<UserDto> getAllUsers(Integer pageSize,
                                 Integer page,
                                 String username,
                                 Boolean active);

    //Create Methods
    UserEntity saveUser(UserEntity user);

    //Update Methods
    UserEntity updateUser(UserPatchDto userPatchDto, UUID userId);

    //Delete Methods
    void deleteUser(UUID userId);

}
