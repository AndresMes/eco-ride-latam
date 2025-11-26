package edu.unimagdalena.apigateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;

@Configuration
public class RouteConfig {

    private final RedisRateLimiter redisRateLimiter;
    private final RedisRateLimiter strictRedisRateLimiter;

    public RouteConfig(
            RedisRateLimiter redisRateLimiter,
            @Qualifier("strictRedisRateLimiter") RedisRateLimiter strictRedisRateLimiter) {
        this.redisRateLimiter = redisRateLimiter;
        this.strictRedisRateLimiter = strictRedisRateLimiter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // ==================== PASSENGER SERVICE ====================
                .route("passenger-service-route", r -> r
                        .path("/api/passengers/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("passenger-service")
                                        .setFallbackUri("forward:/fallback/passenger-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(exchange -> {
                                            // Usa IP como key
                                            String ip = exchange.getRequest()
                                                    .getRemoteAddress()
                                                    .getAddress()
                                                    .getHostAddress();
                                            return reactor.core.publisher.Mono.just(ip);
                                        })
                                )
                        )
                        .uri("lb://PASSENGER-SERVICE")
                )

                .route("driver-service-route", r -> r
                        .path("/api/drivers/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE", "PATCH")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("passenger-service")
                                        .setFallbackUri("forward:/fallback/passenger-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                )
                        )
                        .uri("lb://PASSENGER-SERVICE")
                )

                .route("ratings-route", r -> r
                        .path("/api/ratings/**")
                        .and()
                        .method("GET", "POST")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("passenger-service")
                                        .setFallbackUri("forward:/fallback/passenger-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                )
                        )
                        .uri("lb://PASSENGER-SERVICE")
                )

                // ==================== TRIP SERVICE ====================
                .route("trip-service-route", r -> r
                        .path("/api/trips/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE", "PATCH")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .addRequestHeader("X-Request-Source", "api-gateway")
                                .addResponseHeader("X-Gateway", "eco-ride")
                                .circuitBreaker(c -> c
                                        .setName("trip-service")
                                        .setFallbackUri("forward:/fallback/trip-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                )
                        )
                        .uri("lb://TRIP-SERVICE")
                )

                // ==================== PAYMENT SERVICE (Rate Limit Estricto) ====================
                .route("payment-service-route", r -> r
                        .path("/api/payments/**")
                        .and()
                        .method("GET", "POST")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("payment-service")
                                        .setFallbackUri("forward:/fallback/payment-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(strictRedisRateLimiter) // Límite más estricto
                                )
                        )
                        .uri("lb://PAYMENT-SERVICE")
                )

                // ==================== TEST ROUTE (para probar rate limiting) ====================
                .route("test-fast-route", r -> r
                        .path("/api/test/fast")
                        .filters(f -> f
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(exchange -> {
                                            String key = exchange.getRequest()
                                                    .getHeaders()
                                                    .getFirst("X-User-ID");
                                            if (key == null) {
                                                key = "test-user";
                                            }
                                            return reactor.core.publisher.Mono.just(key);
                                        })
                                )
                                .rewritePath("/api/test/(?<segment>.*)", "/test/${segment}")
                        )
                        .uri("http://localhost:8080")
                )

                .build();
    }
}