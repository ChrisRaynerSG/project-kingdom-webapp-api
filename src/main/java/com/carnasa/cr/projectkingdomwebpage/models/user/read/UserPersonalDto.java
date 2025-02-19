package com.carnasa.cr.projectkingdomwebpage.models.user.read;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class UserPersonalDto {
    private String username;
    private String nickname;
    private String email;
    private Boolean active;
    private Set<String> roles;
    private UserSocialsDto socials;
    private UserContactDetailsDto contactDetails;
    private String bio;
    private String avatar;
    private String firstName;
    private String middleNames;
    private String lastName;
    private String gender;
    private Date birthday;
    private LocalDateTime joinedAt;

    public UserPersonalDto(String username, String nickname, String email, Boolean active, Set<String> roles, UserSocialsDto socials, UserContactDetailsDto contactDetails, String bio, String avatar, String firstName, String middleNames, String lastName, String gender, Date birthday, LocalDateTime joinedAt) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.active = active;
        this.roles = roles;
        this.socials = socials;
        this.contactDetails = contactDetails;
        this.bio = bio;
        this.avatar = avatar;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.joinedAt = joinedAt;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public UserSocialsDto getSocials() {
        return socials;
    }

    public void setSocials(UserSocialsDto socials) {
        this.socials = socials;
    }

    public UserContactDetailsDto getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(UserContactDetailsDto contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(String middleNames) {
        this.middleNames = middleNames;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
