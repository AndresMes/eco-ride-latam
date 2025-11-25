package edu.unimagdalena.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security Configuration for API Gateway
 *
 * Integrates with Keycloak for OAuth2/OIDC authentication and authorization
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final KeycloakJwtAuthenticationConverter jwtAuthenticationConverter;

    public SecurityConfig(KeycloakJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    /**
     * Security configuration for production/dev with Keycloak
     *
     * Features:
     * - JWT authentication with custom role extraction
     * - Public access to actuator endpoints
     * - All other endpoints require authentication
     */
    @Bean
    @Profile("!no-security")
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                // Disable CSRF (stateless REST API)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Configure authorization rules
                .authorizeExchange(exchange -> exchange
                        // Public endpoints - no authentication required
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/actuator/health/**").permitAll()

                        // All other endpoints require authentication
                        .anyExchange().authenticated()
                )

                // Configure OAuth2 Resource Server with custom JWT converter
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                // Use custom JWT authentication converter to extract roles
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                );

        return http.build();
    }

    /**
     * Permissive security configuration for local development without Keycloak
     *
     * Use with profile: no-security
     */
    @Bean
    @Profile("no-security")
    public SecurityWebFilterChain noSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll()
                );

        return http.build();
    }
}