package com.carnasa.cr.projectkingdomwebpage.utils;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserContactDetails;
import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.entities.user.UserSocials;
import com.carnasa.cr.projectkingdomwebpage.models.user.read.*;

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

    public static UserPersonalDto toPersonalDto(UserEntity userEntity){
        return new UserPersonalDto(
                userEntity.getUsername(),
                userEntity.getUserExtra().getNickname(),
                userEntity.getEmail(),
                userEntity.getActive(),
                userEntity.getRoles(),
                toSocialsDto(userEntity.getUserExtra().getSocialLinks()),
                toContactDetailsDto(userEntity.getUserExtra().getContactDetails()),
                userEntity.getUserExtra().getBio(),
                userEntity.getUserExtra().getAvatar(),
                userEntity.getUserExtra().getFirstName(),
                userEntity.getUserExtra().getMiddleNames(),
                userEntity.getUserExtra().getLastName(),
                "Placeholder",
                userEntity.getUserExtra().getBirthday(),
                userEntity.getCreatedAt()



        );
    }

    public static UserDetailDtoAdmin toAdminDto(UserEntity userEntity){
        return new UserDetailDtoAdmin(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getRoles(),
                userEntity.getActive()
        );
    }

    public static UserSocialsDto toSocialsDto(UserSocials userSocials){
        return new UserSocialsDto(
                userSocials.getFacebookUrl(),
                userSocials.getYoutubeUrl(),
                "Instagram placeholder",
                userSocials.getxUrl(),
                userSocials.getTikTokUrl(),
                userSocials.getSteamUrl(),
                userSocials.getDiscordName()
        );
    }

    public static UserContactDetailsDto toContactDetailsDto(UserContactDetails userContactDetails){
        return new UserContactDetailsDto(
                userContactDetails.getAddress1(),
                userContactDetails.getAddress2(),
                userContactDetails.getCity(),
                userContactDetails.getCountyState(),
                userContactDetails.getPostalCode(),
                userContactDetails.getCountry(),
                userContactDetails.getTelephone(),
                userContactDetails.getMobile()
        );
    }
}
