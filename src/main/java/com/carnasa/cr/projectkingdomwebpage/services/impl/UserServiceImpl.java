package com.carnasa.cr.projectkingdomwebpage.services.impl;

import com.carnasa.cr.projectkingdomwebpage.entities.user.*;
import com.carnasa.cr.projectkingdomwebpage.exceptions.BadRequestException;
import com.carnasa.cr.projectkingdomwebpage.exceptions.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.exceptions.UserDetailsAlreadyExistException;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserPatchDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserPostDto;
import com.carnasa.cr.projectkingdomwebpage.repositories.specifications.UserSpecification;
import com.carnasa.cr.projectkingdomwebpage.repositories.user.*;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.ServiceUtils;
import com.carnasa.cr.projectkingdomwebpage.utils.UserUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
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

import static com.carnasa.cr.projectkingdomwebpage.repositories.specifications.UserSpecification.getUserByActive;

@Service
public class UserServiceImpl implements UserService {

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
    @Transactional
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
    public Page<UserDto> getAllUsers(Integer pageSize, Integer page, String username, Boolean active) {
        PageRequest pageRequest = ServiceUtils.buildPageRequest(page, pageSize);
//        if(username != null){
//            if (active != null) {
//                if(active){
//                    return userEntityRepository.findAllByActiveTrueAndUsernameContainingIgnoreCase(username,pageRequest).map(UserUtils::toDto);
//                }
//                else {
//                    return userEntityRepository.findAllByActiveFalseAndUsernameContainingIgnoreCase(username, pageRequest).map(UserUtils::toDto);
//                }
//            }
//            return userEntityRepository.findAllByUsernameContainingIgnoreCase(username,pageRequest).map(UserUtils::toDto);
//        }
//        if(active != null){
//            if(active){
//                return userEntityRepository.findAllByActiveTrue(pageRequest).map(UserUtils::toDto);
//            }
//            else {
//                return userEntityRepository.findAllByActiveFalse(pageRequest).map(UserUtils::toDto);
//            }
//        }

        Specification<UserEntity> spec = Specification.where(UserSpecification.getUserByActive(active)).and(UserSpecification.getUserByUsername(username));

        //If no request parameters just return the whole search
        return userEntityRepository.findAll(spec, pageRequest).map(UserUtils::toDto);
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
        userUpdated |= updateField(userPatchDto.getPassword(), password -> userEntity.setPassword(passwordEncoder.encode(password)));

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
            throw new UserDetailsAlreadyExistException("Username: " + userEntity.getUsername() + " already in use.");
        }
        if(getUserByEmail(userEntity.getEmail()).isPresent()){
            throw new UserDetailsAlreadyExistException("Email: " + userEntity.getEmail().toLowerCase() + " already in use.");
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
}
