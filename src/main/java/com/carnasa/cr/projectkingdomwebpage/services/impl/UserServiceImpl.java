package com.carnasa.cr.projectkingdomwebpage.services.impl;

import com.carnasa.cr.projectkingdomwebpage.entities.user.*;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.BadRequestException;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.ConflictException;
import com.carnasa.cr.projectkingdomwebpage.models.user.read.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.update.UserPatchDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.create.UserPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.update.UserRolePatchDto;
import com.carnasa.cr.projectkingdomwebpage.repositories.specifications.UserSpecification;
import com.carnasa.cr.projectkingdomwebpage.repositories.user.*;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.ServiceUtils;
import com.carnasa.cr.projectkingdomwebpage.utils.UserUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class UserServiceImpl implements UserService {

    public static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserEntityRepository userEntityRepository;
    private final UserExtraRepository userExtraRepository;
    private final UserContactDetailsRepository userContactDetailsRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserSocialsRepository userSocialsRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserEntityRepository userEntityRepository, UserExtraRepository userExtraRepository,
                           UserContactDetailsRepository userContactDetailsRepository, UserPreferencesRepository userPreferencesRepository,
                           UserSocialsRepository userSocialsRepository, PasswordEncoder passwordEncoder) {

        this.userEntityRepository = userEntityRepository;
        this.userExtraRepository = userExtraRepository;
        this.userContactDetailsRepository = userContactDetailsRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.userSocialsRepository = userSocialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserEntity> getUserById(UUID userId) {
        return userEntityRepository.findById(userId);
    }

    @Override
    public Optional<UserDto> getUserDtoById(UUID userId) {
        return getUserById(userId).map(UserUtils::toDto);
    }

    private Optional<UserEntity> getUserByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    private Optional<UserEntity> getUserByUsername(String username) {
        return userEntityRepository.findByUsername(username);
    }

    private Optional<UserExtra> getUserExtraByUserId(UUID userId) {
        return getUserById(userId).map(UserEntity::getUserExtra);
    }

    @Override
    public Optional<UserEntity> getByUsername(String username) {
        return userEntityRepository.findByUsername(username);
    }

    @Override
    public Page<UserEntity> getAllUsers(Integer pageSize, Integer page, String username, Boolean active) {
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, pageSize);
        Specification<UserEntity> spec = Specification.where(UserSpecification.getUserByActive(active)).and(UserSpecification.getUserByUsername(username));
        return userEntityRepository.findAll(spec, pageRequest);
    }

    @Override
    @Transactional
    public UserEntity saveUser(UserPostDto userDto) {

        try {

            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                throw new BadRequestException("Passwords don't match");
            }

            //@todo validate password strength here.

            UserEntity user = new UserEntity();
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail().toLowerCase());
            user.setPassword(userDto.getPassword());

            validateNewUser(user);

            user.setCreatedAt(LocalDateTime.now());
            user.setLastModified(LocalDateTime.now());
            user.setActive(true);

            user.getRoles().add("ROLE_USER");

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            UserEntity savedUser = userEntityRepository.save(user);
            userEntityRepository.flush();

            createNewUserExtra(savedUser);
            return savedUser;
        }
        catch (ConstraintViolationException e){
            throw new BadRequestException("INVALID EMAIL: " + userDto.getEmail().toLowerCase() + " Please enter a valid email address");
        }
    }

    @Override
    @Transactional
    public UserEntity updateUser(UserPatchDto userPatchDto, UUID userId) {
        UserEntity userEntity = getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        UserExtra userExtra = userEntity.getUserExtra();
        UserContactDetails contactDetails = userExtra.getContactDetails();
        UserPreferences userPreferences = userExtra.getUserPreferences();
        UserSocials socialLinks = userExtra.getSocialLinks();

        LocalDateTime now = LocalDateTime.now();

        boolean userUpdated = false;
        boolean userExtraUpdated = false;
        boolean contactDetailsUpdated = false;
        boolean userPreferencesUpdated = false;
        boolean socialLinksUpdated = false;

        userUpdated |= updateField(userPatchDto.getActive(), userEntity::setActive);

        userExtraUpdated |= updateField(userPatchDto.getFirstName(), userExtra::setFirstName);
        userExtraUpdated |= updateField(userPatchDto.getLastName(), userExtra::setLastName);
        userExtraUpdated |= updateField(userPatchDto.getMiddleNames(), userExtra::setMiddleNames);
        userExtraUpdated |= updateField(userPatchDto.getNickname(), userExtra::setNickname);
        userExtraUpdated |= updateField(userPatchDto.getBio(), userExtra::setBio);
        userExtraUpdated |= updateField(userPatchDto.getBirthday(), userExtra::setBirthday);
        userExtraUpdated |= updateField(userPatchDto.getAvatar(), userExtra::setAvatar);

        contactDetailsUpdated |= updateField(userPatchDto.getAddress1(), contactDetails::setAddress1);
        contactDetailsUpdated |= updateField(userPatchDto.getAddress2(), contactDetails::setAddress2);
        contactDetailsUpdated |= updateField(userPatchDto.getCity(), contactDetails::setCity);
        contactDetailsUpdated |= updateField(userPatchDto.getCountyState(), contactDetails::setCountyState);
        contactDetailsUpdated |= updateField(userPatchDto.getPostalCode(), contactDetails::setPostalCode);
        contactDetailsUpdated |= updateField(userPatchDto.getCountry(), contactDetails::setCountry);
        contactDetailsUpdated |= updateField(userPatchDto.getTelephone(), contactDetails::setTelephone);
        contactDetailsUpdated |= updateField(userPatchDto.getMobile(), contactDetails::setMobile);

        userPreferencesUpdated |= updateField(userPatchDto.getDarkMode(), userPreferences::setDarkMode);

        socialLinksUpdated |= updateField(userPatchDto.getFacebookUrl(), socialLinks::setFacebookUrl);
        socialLinksUpdated |= updateField(userPatchDto.getYoutubeUrl(), socialLinks::setYoutubeUrl);
        socialLinksUpdated |= updateField(userPatchDto.getxUrl(), socialLinks::setxUrl);
        socialLinksUpdated |= updateField(userPatchDto.getSteamUrl(), socialLinks::setSteamUrl);
        socialLinksUpdated |= updateField(userPatchDto.getTikTokUrl(), socialLinks::setTikTokUrl);
        socialLinksUpdated |= updateField(userPatchDto.getDiscordName(), socialLinks::setDiscordName);

        if (userUpdated){
            userEntity.setLastModified(now);
        }
        if (userExtraUpdated){
            userExtra.setLastModified(now);
        }
        if (contactDetailsUpdated){
            contactDetails.setLastModified(now);
        }
        if (userPreferencesUpdated){
            userPreferences.setLastModified(now);
        }
        if (socialLinksUpdated) socialLinks.setLastModified(now);

        return userEntityRepository.save(userEntity);
    }

    private <T> boolean updateField(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        try{
            Optional<UserEntity> userEntityOptional = getUserById(userId);
            if (userEntityOptional.isEmpty()) {
                throw new NotFoundException("User with ID " + userId + " not found");
            }

            UserEntity userEntity = userEntityOptional.get();
            Optional<UserExtra> userExtraOptional = getUserExtraByUserId(userId);

            if (userExtraOptional.isPresent()) {
                // Delete dependent entities
                userSocialsRepository.deleteByUserExtra(userExtraOptional.get());
                userPreferencesRepository.deleteByUserExtra(userExtraOptional.get());
                userContactDetailsRepository.deleteByUserExtra(userExtraOptional.get());

                // Now delete UserExtra
                userExtraRepository.delete(userExtraOptional.get());
            }
            // Finally, delete UserEntity
            userEntityRepository.delete(userEntity);
        }
        catch(Exception e){
            throw new RuntimeException("Unable to delete user with ID: " + userId);
        }
    }

    private void validateNewUser(UserEntity userEntity){

        // first check to see if username or email already exists in the database
        if(getUserByUsername(userEntity.getUsername()).isPresent()){
            throw new ConflictException("Username: " + userEntity.getUsername() + " already in use.");
        }
        if(getUserByEmail(userEntity.getEmail()).isPresent()){
            throw new ConflictException("Email: " + userEntity.getEmail().toLowerCase() + " already in use.");
        }
        //then validate fields to make sure they are acceptable to be saved
        //@Todo remove todos
    }

    private void createNewUserExtra(UserEntity user){
        UserExtra userExtra = new UserExtra();

            userExtra.setUser(user);
            userExtra.setCreatedAt(LocalDateTime.now());
            userExtra.setLastModified(LocalDateTime.now());


        UserExtra ue = userExtraRepository.save(userExtra);
        userExtraRepository.flush();

        user.setUserExtra(ue);
        userEntityRepository.save(user);
        userEntityRepository.flush();

        UserPreferences up = createNewUserPreferences(userExtra);
        UserSocials us = createNewUserSocials(userExtra);
        UserContactDetails uc = createNewUserContactDetails(userExtra);

        ue.setUserPreferences(up);
        ue.setContactDetails(uc);
        ue.setSocialLinks(us);

        userExtraRepository.saveAndFlush(userExtra);
    }

    private UserContactDetails createNewUserContactDetails(UserExtra userExtra) {
        UserContactDetails userContactDetails = new UserContactDetails();
        userContactDetails.setUserExtra(userExtra);
        userContactDetails.setCreatedAt(LocalDateTime.now());
        userContactDetails.setLastModified(LocalDateTime.now());
        return userContactDetailsRepository.saveAndFlush(userContactDetails);

    }
    private UserSocials createNewUserSocials(UserExtra userExtra) {
        UserSocials userSocials = new UserSocials();
        userSocials.setUserExtra(userExtra);
        userSocials.setCreatedAt(LocalDateTime.now());
        userSocials.setLastModified(LocalDateTime.now());
        return userSocialsRepository.saveAndFlush(userSocials);

    }
    private UserPreferences createNewUserPreferences(UserExtra userExtra) {
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUserExtra(userExtra);
        userPreferences.setCreatedAt(LocalDateTime.now());
        userPreferences.setLastModified(LocalDateTime.now());
        return userPreferencesRepository.saveAndFlush(userPreferences);
    }

    /**
     * @param userId id of user to be patched
     * @param userPatchDto Dto Object containing nullable fields of isAdmin, isDeveloper, isModerator
     * @return UserDto containing information on user that was updated
     * @throws NotFoundException if user not found
     */
    @Override
    public UserDto patchUserRoles(UUID userId, UserRolePatchDto userPatchDto) {

        log.info("Entered UserService.patchUserRoles()");
        Optional<UserEntity> userEntityOptional = getUserById(userId);

        boolean userUpdated = false;

        if (userEntityOptional.isEmpty()) {
            log.error("User with ID: " + userId + " not found");
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        UserEntity userEntity = userEntityOptional.get();

        if(userPatchDto.getAdmin()!= null){
            log.debug("User admin role changed");
            if(userPatchDto.getAdmin()){
                userEntity.getRoles().add("ROLE_ADMIN");
            }
            else{
                userEntity.getRoles().remove("ROLE_ADMIN");
            }
            userUpdated = true;
        }
        if(userPatchDto.getDeveloper()!= null){
            log.debug("User developer role changed");
            if(userPatchDto.getDeveloper()){
                userEntity.getRoles().add("ROLE_DEVELOPER");
            }
            else{
                userEntity.getRoles().remove("ROLE_DEVELOPER");
            }
        }
        if(userPatchDto.getModerator()!= null){
            log.debug("User moderator role changed");
            if(userPatchDto.getModerator()){
                userEntity.getRoles().add("ROLE_MODERATOR");
            }
            else{
                userEntity.getRoles().remove("ROLE_MODERATOR");
            }
        }

        if(userUpdated){
            log.info("Attempting to save updated user roles to database");
            userEntity.setLastModified(LocalDateTime.now());
            try {
                UserDto user = UserUtils.toDto(userEntityRepository.save(userEntity));
                log.info("Successfully saved updated user roles to database");
                return user;
            }
            catch(Exception e){
                log.error("Failed to save updated user roles to database with error message: {}", e.getMessage());
            }
        }
        return null;
    }
    /**
     * Method to set last login time of user when they log in to the web app
     * @param username username of user logging in
     */
    @Override
    public void userLoggedIn(String username) {
        log.info("Entered UserService.userLoggedIn()");
        UserEntity user = getUserByUsername(username).orElseThrow(()->new NotFoundException("User: " + username + " not found"));
        user.setLastLogin(LocalDateTime.now());
        userEntityRepository.save(user);
        log.info("Saved last login for user: {} at {}", username, user.getLastLogin());
    }
}
