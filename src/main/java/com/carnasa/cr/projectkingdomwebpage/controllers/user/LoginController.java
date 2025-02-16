package com.carnasa.cr.projectkingdomwebpage.controllers.user;

import com.carnasa.cr.projectkingdomwebpage.models.user.UserDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserLoginDto;
import com.carnasa.cr.projectkingdomwebpage.models.user.UserPostDto;
import com.carnasa.cr.projectkingdomwebpage.security.JwtUtils;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.UserService;
import com.carnasa.cr.projectkingdomwebpage.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

import static com.carnasa.cr.projectkingdomwebpage.controllers.devlog.DevLogPostController.BASE_URL;


@RestController
public class LoginController {

    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String REGISTER_URL = BASE_URL + "/register";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public LoginController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(REGISTER_URL)
    public ResponseEntity<UserDto> createUser(@RequestBody UserPostDto userCredentials) {
        UserDto user = UserUtils.toDto(userService.saveUser(userCredentials));
        URI location = URI.create(BASE_URL+"/users/" + user.getId());
        return ResponseEntity.created(location).body(user);
    }

    @PostMapping(LOGIN_URL)
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDto userCredentials, BindingResult bindingResult) {

        try{
            authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(userCredentials.getUsername(), userCredentials.getPassword()));
            String token = jwtUtils.generateToken(userCredentials.getUsername());
            return ResponseEntity.ok().body(Map.of("token", token));
        }
        catch (Exception ex) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
