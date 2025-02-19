package com.carnasa.cr.projectkingdomwebpage.models.user.read;

import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostReplyDto;

import java.util.List;
import java.util.UUID;

public class UserDetailDto {
    private String username;
    private String bio;
    private String nickname;
    private String avatar;
    private UserSocialsDto userSocials;
    //private List<DevLogPostReplyDto> recentReplies;


    public UserDetailDto(String username,
                         String bio,
                         String nickname,
                         String avatar,
                         UserSocialsDto userSocials) {
        this.username = username;
        this.bio = bio;
        this.nickname = nickname;
        this.avatar = avatar;
        this.userSocials = userSocials;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserSocialsDto getUserSocials() {
        return userSocials;
    }

    public void setUserSocials(UserSocialsDto userSocials) {
        this.userSocials = userSocials;
    }
}
