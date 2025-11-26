package edu.unimagdalena.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class RateLimiterConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            // Intenta obtener IP real
            String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");

            if (ip == null || ip.isEmpty()) {
                ip = Objects.requireNonNull(
                        exchange.getRequest().getRemoteAddress(),
                        "Remote address is null"
                ).getAddress().getHostAddress();
            }

            // Log para debugging
            String finalIp = ip;
            return Mono.just(finalIp)
                    .doOnNext(key ->
                            log.info("Rate Limiter Key (IP): {}", key)
                    );
        };
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-User-ID");

            // Fallback a IP si no hay header
            if (userId == null || userId.isEmpty()) {
                userId = Objects.requireNonNull(
                        exchange.getRequest().getRemoteAddress()
                ).getAddress().getHostAddress();
            }

            String finalUserId = userId;
            return Mono.just(finalUserId)
                    .doOnNext(key ->
                            log.info("Rate Limiter Key (User): {}", key)
                    );
        };
    }

    @Bean
    @Primary  // ← Este es el bean por defecto
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(
                5,   // replenishRate: tokens por segundo
                10,  // burstCapacity: máximo de tokens acumulables
                1    // requestedTokens: tokens por request
        );
    }

    @Bean("strictRedisRateLimiter")
    public RedisRateLimiter strictRedisRateLimiter() {
        return new RedisRateLimiter(
                2,   // Solo 2 peticiones/segundo
                5,   // Burst máximo de 5
                1
        );
    }
}