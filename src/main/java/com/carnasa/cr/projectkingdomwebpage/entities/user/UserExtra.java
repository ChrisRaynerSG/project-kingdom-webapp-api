package com.carnasa.cr.projectkingdomwebpage.entities.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "user_extra", schema = "project-kingdom-webapp-db")
public class UserExtra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_extra_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_names")
    private String middleNames;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "bio")
    private String bio;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name="avatar_picture")
    private String avatar;

    @OneToOne
    @JoinColumn(name = "user_contact_details")
    private UserContactDetails contactDetails;

    @OneToOne
    @JoinColumn(name = "user_socials")
    private UserSocials socialLinks;

    @OneToOne
    @JoinColumn(name = "user_preferences_id")
    private UserPreferences userPreferences;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(String middleNames) {
        this.middleNames = middleNames;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UserContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(UserContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    public UserSocials getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(UserSocials socialLinks) {
        this.socialLinks = socialLinks;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
