package com.carnasa.cr.projectkingdomwebpage.models.user.read;

import java.util.Set;
import java.util.UUID;

public class UserDetailDtoAdmin {
    private UUID userId;
    private String username;
    private String email;
    private Set<String> roles;
    private Boolean active;

    public UserDetailDtoAdmin(
            UUID userId,
            String username,
            String email,
            Set<String> roles,
            Boolean active) {

        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.active = active;
    }
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
