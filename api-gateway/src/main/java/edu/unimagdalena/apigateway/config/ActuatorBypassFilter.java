package edu.unimagdalena.apigateway.config;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ActuatorBypassFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Si es actuator, dejar pasar sin routing del gateway
        if (path.startsWith("/actuator/")) {
            // Simplemente continuar el chain normal sin marcar nada
            // Esto permite que Spring Boot maneje directamente el endpoint
            return chain.filter(exchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Ejecutar ANTES del Gateway routing
        return -1; // Antes que Ordered.HIGHEST_PRECEDENCE del gateway
    }
}