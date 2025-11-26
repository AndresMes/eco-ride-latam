package edu.unimagdalena.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final KeycloakJwtAuthenticationConverter jwtAuthenticationConverter;
    private final SavedRequestAwareAuthenticationSuccessHandler successHandler;

    public SecurityConfig(
            KeycloakJwtAuthenticationConverter jwtAuthenticationConverter,
            SavedRequestAwareAuthenticationSuccessHandler successHandler) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.successHandler = successHandler;
    }

    @Bean
    @Profile("!no-security")
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange
                        // Public endpoints
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers("/login/**").permitAll()
                        .pathMatchers("/oauth2/**").permitAll()
                        // All other endpoints require authentication
                        .anyExchange().authenticated()
                )

                // Entry point: redirect to OAuth2 login
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(
                                new RedirectServerAuthenticationEntryPoint("/oauth2/authorization/eco-gateway")
                        )
                )

                // OAuth2 Login configuration with custom success handler
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler)  // ← CAMBIO AQUÍ
                )

                // OAuth2 Resource Server - Valida JWT tokens
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )

                // Logout configuration
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                );

        return http.build();
    }

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

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler successHandler =
                new RedirectServerLogoutSuccessHandler();
        successHandler.setLogoutSuccessUrl(
                URI.create("http://localhost:9090/realms/ecoride/protocol/openid-connect/logout?redirect_uri=http://localhost:8080")
        );
        return successHandler;
    }
}