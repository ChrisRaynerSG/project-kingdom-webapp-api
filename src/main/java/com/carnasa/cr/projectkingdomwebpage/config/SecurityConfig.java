package com.carnasa.cr.projectkingdomwebpage.config;

import com.carnasa.cr.projectkingdomwebpage.security.JwtAuthenticationFilter;
import com.carnasa.cr.projectkingdomwebpage.services.impl.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.carnasa.cr.projectkingdomwebpage.utils.UrlUtils.*;


/**
 * SecurityConfig is a configuration class for setting up application security.
 * It defines beans and configurations for password encoding,
 * HTTP security settings, and authentication management.
 *
 * The class integrates with JWT for stateless authentication and relies on
 * a custom implementation of UserDetailsService for loading user-specific data.
 *
 * An instance of JwtAuthenticationFilter is injected to enable token-based
 * security in the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    public static Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter){

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;


    }

    /**
     * Configures the password encoder bean using BCrypt algorithm.
     * This encoder is used to hash passwords securely.
     * 
     * @return an instance of BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * Configures HTTP security settings for the application.
     * This method sets up JWT authentication, CORS support, CSRF disabling, session management,
     * and authorization rules for various HTTP request patterns.
     * 
     * @param http the {@link HttpSecurity} object to configure.
     * @return a {@link SecurityFilterChain} defining the security configuration.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        
        
        logger.trace("Configuring SecurityFilterChain");
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(httpRequest ->
                    httpRequest
                            .requestMatchers(
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/swagger-resources/**",
                                    "/webjars/**"
                            )
                            .permitAll()
                            .requestMatchers(HttpMethod.POST,
                                    BASE_URL + "/register",
                                    BASE_URL + "/login")
                            .permitAll()
                            .requestMatchers(HttpMethod.POST).hasAuthority("ROLE_USER") // depending on what is needed to be posted change this in controllers?
                            .requestMatchers(HttpMethod.PATCH).hasAuthority("ROLE_USER")
                            .requestMatchers(HttpMethod.PUT).hasAuthority("ROLE_USER")
                            .requestMatchers(HttpMethod.DELETE).hasAuthority("ROLE_USER")
                            .requestMatchers(ADMIN_URL + "/**").hasAuthority("ROLE_ADMIN")
                            .requestMatchers(HttpMethod.GET).permitAll()
                            .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configures the AuthenticationManager bean.
     * This manager handles authentication using a custom user details service
     * and a BCrypt password encoder.
     * 
     * @param userDetailsService the service to load user details.
     * @param passwordEncoder the service to encode passwords.
     * @return an instance of {@link AuthenticationManager}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsServiceImpl userDetailsService,
            PasswordEncoder passwordEncoder) throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        logger.trace("Configuring AuthenticationManager");
        return new ProviderManager(daoAuthenticationProvider);
    }
    /**
     * Configures the CORS settings to allow requests from the frontend.
     * Allows all origins, methods, and headers, while permitting
     * credentials like cookies and Authorization headers.
     * 
     * @return an instance of {@link CorsConfigurationSource}.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
