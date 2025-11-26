package edu.unimagdalena.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class RateLimiterConfig {

    /**
     * KeyResolver basado en IP del cliente
     * Cada IP tiene su propio límite de rate
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                    .getAddress()
                    .getHostAddress();

            return Mono.just(ip);
        };
    }

    /**
     * Configuración de RedisRateLimiter
     * 10 requests por minuto por IP
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 10, 1);
        // replenishRate: 10 tokens/min
        // burstCapacity: 10 tokens máximo
        // requestedTokens: 1 token por request
    }
}