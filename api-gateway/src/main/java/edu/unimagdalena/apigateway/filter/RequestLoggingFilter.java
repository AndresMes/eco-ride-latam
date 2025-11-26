package edu.unimagdalena.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        // Log del request entrante
        log.info("==> Request: {} {} | Correlation-ID: {} | Client: {}",
                request.getMethod(),
                request.getPath(),
                request.getHeaders().getFirst("X-Correlation-ID"),
                request.getRemoteAddress()
        );

        // Continuar con la cadena y loggear al finalizar
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value()
                    : 0;

            log.info("<== Response: {} {} | Status: {} | Duration: {}ms",
                    request.getMethod(),
                    request.getPath(),
                    statusCode,
                    duration
            );
        }));
    }

    @Override
    public int getOrder() {
        return 0; // Ejecutar después de CorrelationIdFilter
    }
}