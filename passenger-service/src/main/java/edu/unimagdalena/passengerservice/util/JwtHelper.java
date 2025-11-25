package edu.unimagdalena.passengerservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {

    public static String extractSubject(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getSubject();
        }
        throw new IllegalStateException("Authentication is not a JWT token");
    }


    public static String extractEmail(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("email");
        }
        throw new IllegalStateException("Authentication is not a JWT token");
    }


    public static String extractPreferredUsername(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaimAsString("preferred_username");
        }
        throw new IllegalStateException("Authentication is not a JWT token");
    }

    public static Jwt getJwt(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new IllegalStateException("Authentication is not a JWT token");
    }
}