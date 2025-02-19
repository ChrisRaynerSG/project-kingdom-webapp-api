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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.carnasa.cr.projectkingdomwebpage.utils.UrlUtils.*;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        logger.trace("Configuring SecurityFilterChain");
        return http.
                csrf(AbstractHttpConfigurer::disable)
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
}
