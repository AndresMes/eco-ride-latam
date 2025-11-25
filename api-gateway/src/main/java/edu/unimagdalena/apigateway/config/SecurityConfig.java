package edu.unimagdalena.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Security configuration for production/dev with Keycloak
     */
    @Bean
    @Profile("!no-security")
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        /*
        /http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

        return http.build();
         */
        http.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults());
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();

    }

    /**
     * Permissive security configuration for local development without Keycloak
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