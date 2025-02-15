package com.carnasa.cr.projectkingdomwebpage.utils;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserDto;

import java.util.UUID;

public class UserUtils {

    public static UserDto toDto(UserEntity userEntity){

        return new UserDto(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getUserExtra() != null ? userEntity.getUserExtra().getNickname() : null,
                userEntity.getUserExtra() != null ? userEntity.getUserExtra().getAvatar() : null
        );
    }

    public static UUID generateUUID(){
        return UUID.randomUUID();
    }

}
