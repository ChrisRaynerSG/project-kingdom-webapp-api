package com.carnasa.cr.projectkingdomwebpage.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtils{

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private Long jwtExpirationMs;

    public static Logger log = LoggerFactory.getLogger(JwtUtils.class);


    public String generateToken(String username, Set<String> roles, String userId) {
        log.trace("Generating JWT token for user {}", username);
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();

//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
    }

    public String extractUsername(String token) {
        log.trace("Extracting username from JWT token {}", token);
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        log.trace("Extracting roles from JWT token {}", token);
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }

    public String extractUserId(String token) {
        log.trace("Extracting user id from JWT token {}", token);
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claim = parseClaims(token);
        return claimsResolver.apply(claim);
    }

    public boolean validateToken(String token) {
        log.trace("Validating JWT token {}", token);
        try{
            parseClaims(token);
            log.trace("JWT token validated");
            return true;
        }
        catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT token validation failed for token {} with error: {}", token, e.getMessage());
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
