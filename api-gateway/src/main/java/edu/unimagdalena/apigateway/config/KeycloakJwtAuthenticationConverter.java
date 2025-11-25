package edu.unimagdalena.apigateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JWT Authentication Converter for Keycloak
 *
 * Extracts roles from Keycloak JWT token and converts them to Spring Security GrantedAuthorities.
 *
 * Keycloak stores roles in two places:
 * 1. realm_access.roles - Realm-level roles (ROLE_DRIVER, ROLE_PASSENGER, ROLE_ADMIN)
 * 2. resource_access.{client-id}.roles - Client-specific roles (scopes like trips:write)
 *
 * This converter extracts both and merges them into a single collection of authorities.
 */
@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String RESOURCE_ACCESS_CLAIM = "resource_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String CLIENT_ID = "eco-gateway";
    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * Convert JWT to Authentication Token with extracted authorities
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        // Create authentication token with extracted authorities
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, authorities);

        return Mono.just(authenticationToken);
    }

    /**
     * Extract authorities from JWT claims
     *
     * Merges roles from:
     * - realm_access.roles (realm roles like ROLE_DRIVER)
     * - resource_access.eco-gateway.roles (client roles like trips:write)
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Collection<String> realmRoles = extractRealmRoles(jwt);
        Collection<String> resourceRoles = extractResourceRoles(jwt);

        // Merge both collections and convert to GrantedAuthority
        return Stream.concat(realmRoles.stream(), resourceRoles.stream())
                .distinct()
                .map(this::convertToAuthority)
                .collect(Collectors.toSet());
    }

    /**
     * Extract roles from realm_access claim
     *
     * Structure: { "realm_access": { "roles": ["ROLE_DRIVER", "..."] } }
     */
    @SuppressWarnings("unchecked")
    private Collection<String> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS_CLAIM);

        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }

        Object rolesObj = realmAccess.get(ROLES_CLAIM);

        if (rolesObj instanceof Collection) {
            return (Collection<String>) rolesObj;
        }

        return Collections.emptyList();
    }

    /**
     * Extract roles from resource_access claim for specific client
     *
     * Structure: { "resource_access": { "eco-gateway": { "roles": ["trips:write", "..."] } } }
     */
    @SuppressWarnings("unchecked")
    private Collection<String> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS_CLAIM);

        if (resourceAccess == null || resourceAccess.isEmpty()) {
            return Collections.emptyList();
        }

        Object clientAccessObj = resourceAccess.get(CLIENT_ID);

        if (!(clientAccessObj instanceof Map)) {
            return Collections.emptyList();
        }

        Map<String, Object> clientAccess = (Map<String, Object>) clientAccessObj;
        Object rolesObj = clientAccess.get(ROLES_CLAIM);

        if (rolesObj instanceof Collection) {
            return (Collection<String>) rolesObj;
        }

        return Collections.emptyList();
    }

    /**
     * Convert role string to GrantedAuthority
     *
     * Ensures all roles have ROLE_ prefix for Spring Security compatibility
     * - If role already has ROLE_ prefix: keep as-is
     * - If role is a scope (contains :): keep as-is (e.g., trips:write)
     * - Otherwise: add ROLE_ prefix
     */
    private GrantedAuthority convertToAuthority(String role) {
        // If already has ROLE_ prefix or is a scope, use as-is
        if (role.startsWith(ROLE_PREFIX) || role.contains(":")) {
            return new SimpleGrantedAuthority(role);
        }

        // Otherwise, add ROLE_ prefix
        return new SimpleGrantedAuthority(ROLE_PREFIX + role);
    }
}