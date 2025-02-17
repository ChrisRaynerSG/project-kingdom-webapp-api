package com.carnasa.cr.projectkingdomwebpage.utils;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserDetailsPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;


public class SecurityUtils {

    public static Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    /**
     * Private constructor to ensure no class is instantiated of this utility class
     */
    private SecurityUtils() {
    }
    /**
     * @return User UUID associated with current logged-in user by JWT.
     * @throws RuntimeException if not logged in or authentication not valid
     */
    public static UUID getCurrentUserId() {
        log.info("getCurrentUserId() called");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsPrincipal userDetails) {
            log.info("User authenticated, returning user id.");
            return UUID.fromString(userDetails.getId());
        }
        throw new RuntimeException("User not authenticated");
    }
}
