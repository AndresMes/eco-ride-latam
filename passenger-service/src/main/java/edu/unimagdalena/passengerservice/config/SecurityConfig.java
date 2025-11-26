package edu.unimagdalena.passengerservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**", "/actuator/info").permitAll()
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Set<String> roles = new HashSet<>();

            // 1. Extraer roles del realm (realm_access.roles)
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                Object realmRoles = realmAccess.get("roles");
                if (realmRoles instanceof Collection) {
                    ((Collection<?>) realmRoles).forEach(role ->
                            roles.add(String.valueOf(role))
                    );
                }
            }

            // 2. Extraer roles del cliente (resource_access.{client-id}.roles)
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                resourceAccess.values().forEach(resource -> {
                    if (resource instanceof Map) {
                        Map<?, ?> resourceMap = (Map<?, ?>) resource;
                        Object clientRoles = resourceMap.get("roles");
                        if (clientRoles instanceof Collection) {
                            ((Collection<?>) clientRoles).forEach(role ->
                                    roles.add(String.valueOf(role))
                            );
                        }
                    }
                });
            }

            // 3. Extraer roles directos (si existen)
            List<String> directRoles = jwt.getClaimAsStringList("roles");
            if (directRoles != null) {
                roles.addAll(directRoles);
            }

            // 4. Extraer scopes (opcional)
            String scope = jwt.getClaimAsString("scope");
            if (scope != null) {
                Arrays.stream(scope.split(" "))
                        .forEach(s -> roles.add("SCOPE_" + s));
            }

            // Convertir a GrantedAuthority
            // Si el rol ya empieza con "ROLE_", no lo duplicamos
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(role -> {
                        if (role.startsWith("ROLE_")) {
                            return new SimpleGrantedAuthority(role);
                        }
                        return new SimpleGrantedAuthority("ROLE_" + role);
                    })
                    .collect(Collectors.toSet());

            return authorities;
        });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
