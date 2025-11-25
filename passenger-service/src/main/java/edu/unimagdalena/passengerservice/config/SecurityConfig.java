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
                        .pathMatchers("/actuator/**", "/actuator/health/**").permitAll()
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

            Object realmAccessObj = jwt.getClaim("realm_access");
            if (realmAccessObj instanceof Map) {
                Map<?,?> realmAccess = (Map<?,?>) realmAccessObj;
                Object realmRoles = realmAccess.get("roles");
                if (realmRoles instanceof Collection) {
                    ((Collection<?>) realmRoles).forEach(r -> roles.add(String.valueOf(r)));
                }
            }

            Object resourceAccessObj = jwt.getClaim("resource_access");
            if (resourceAccessObj instanceof Map) {
                Map<?,?> resourceAccess = (Map<?,?>) resourceAccessObj;
                for (Object clientObj : resourceAccess.values()) {
                    if (clientObj instanceof Map) {
                        Object clientRoles = ((Map<?,?>) clientObj).get("roles");
                        if (clientRoles instanceof Collection) {
                            ((Collection<?>) clientRoles).forEach(r -> roles.add(String.valueOf(r)));
                        }
                    }
                }
            }

            String scopeClaim = jwt.getClaimAsString("scope");
            if (scopeClaim == null) {
                Object scp = jwt.getClaim("scp");
                if (scp instanceof Collection) {
                    ((Collection<?>) scp).forEach(s -> roles.add("SCOPE_" + String.valueOf(s)));
                } else if (scp instanceof String) {
                    for (String s : ((String) scp).split(" ")) roles.add("SCOPE_" + s);
                }
            } else {
                for (String s : scopeClaim.split(" ")) roles.add("SCOPE_" + s);
            }

            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(r -> {
                        if (r.startsWith("SCOPE_")) {
                            return new SimpleGrantedAuthority(r);
                        }
                        return new SimpleGrantedAuthority("ROLE_" + r);
                    })
                    .collect(Collectors.toList());

            return authorities;
        });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
