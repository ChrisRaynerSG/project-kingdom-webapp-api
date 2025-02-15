package com.carnasa.cr.projectkingdomwebpage.models.user;

import java.util.UUID;

public class UserDto {

    private UUID id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;

    public UserDto(UUID id,
                   String username,
                   String email
                  ) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UserDto(UUID id,
                   String username,
                   String email,
                   String nickname,
                   String avatar){
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.avatar = avatar;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
