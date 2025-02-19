package com.carnasa.cr.projectkingdomwebpage.controllers.user;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserEntity;
import com.carnasa.cr.projectkingdomwebpage.models.user.read.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.create.UserLoginDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.create.UserPostDto;
import com.carnasa.cr.projectkingdomwebpage.security.JwtUtils;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import static com.carnasa.cr.projectkingdomwebpage.utils.UrlUtils.*;
import static com.carnasa.cr.projectkingdomwebpage.utils.LoggingUtils.*;


@RestController
public class LoginController {

    public static Logger log = LoggerFactory.getLogger(LoginController.class);

    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String REGISTER_URL = BASE_URL + "/register";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public LoginController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        log.trace("Creating login controller");
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(REGISTER_URL)
    public ResponseEntity<UserDto> createUser(@RequestBody UserPostDto userCredentials) {
        log.info(POST_ENDPOINT_LOG_HIT, REGISTER_URL);
        UserDto user = UserUtils.toDto(userService.saveUser(userCredentials));
        URI location = URI.create(BASE_URL+"/users/" + user.getId());
        return ResponseEntity.created(location).body(user);
    }

    @PostMapping(LOGIN_URL)
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto userCredentials, BindingResult bindingResult) {
        log.info(POST_ENDPOINT_LOG_HIT, LOGIN_URL);
        try{
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(userCredentials.getUsername(), userCredentials.getPassword()));

            Optional<UserEntity> user = userService.getByUsername(userCredentials.getUsername());
            if(user.isPresent()){
                String token = jwtUtils.generateToken(userCredentials.getUsername(), user.get().getRoles(), user.get().getId().toString());
                log.info("User: {} Logged in successfully. Token: {}", userCredentials.getUsername(), token);
                userService.userLoggedIn(userCredentials.getUsername());
                return ResponseEntity.ok().body(Map.of("token", token));
            }
            else{
                log.warn("Login attempt failed for username: {}", userCredentials.getUsername());
                return ResponseEntity.status(401).body("Invalid username or password");
            }
        }
        catch (Exception ex) {
            log.warn("Login attempt failed for username: {}", userCredentials.getUsername());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
