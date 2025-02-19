package com.carnasa.cr.projectkingdomwebpage.utils;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserDetailsPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;


public class SecurityUtils {

    public static final Set<String> ADMIN_PERMISSIONS = Set.of("ROLE_ADMIN");
    public static final Set<String> DEVELOPER_PERMISSIONS = Set.of("ROLE_ADMIN", "ROLE_DEVELOPER");
    public static final Set<String> MODERATOR_PERMISSIONS = Set.of("ROLE_ADMIN", "ROLE_MODERATOR");
    public static final Set<String> USER_PERMISSIONS = Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_DEVELOPER");

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
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsPrincipal userDetails) {
            log.info("User authenticated, returning user id.");
            return UUID.fromString(userDetails.getId());
        }
        throw new RuntimeException("User not authenticated");
    }
    /**
     * @return User roles associated with current logged-in user by JWT.
     * @throws RuntimeException if not logged in or authentication not valid
     */
    public static Collection<? extends GrantedAuthority> getCurrentUserRoles(){
        log.info("getCurrentUserRoles() called");
        Authentication authentication = getCurrentAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetailsPrincipal userDetails) {
            log.info("User authenticated, returning user roles.");
            return userDetails.getAuthorities();
        }
        throw new RuntimeException("User not authenticated");
    }
    /**
     * @return current user authentication
     */
    public static Authentication getCurrentAuthentication() {
        log.debug("Fetching current authentication");
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Checks if the currently authenticated user has permission to post DevLogs.
     * @return true if the user has one of the required roles, false otherwise.
     */
    public static boolean hasDevLogPostPermission() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false; // User not authenticated
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Convert authorities to Strings
                .anyMatch(DEVELOPER_PERMISSIONS::contains); // Check if any match
    }
}
