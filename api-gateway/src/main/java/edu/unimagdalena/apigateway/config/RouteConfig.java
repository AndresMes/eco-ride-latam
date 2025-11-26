package edu.unimagdalena.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;

@Configuration
public class RouteConfig {

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
                                .requestRateLimiter().and()
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
                                .requestRateLimiter().and()
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
                                .requestRateLimiter().and()
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
                                .requestRateLimiter().and()
                        )
                        .uri("lb://TRIP-SERVICE")
                )

                .route("trip-search-route", r -> r
                        .path("/api/trips")
                        .and()
                        .method("GET")
                        .and()
                        .query("origin")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("trip-service")
                                        .setFallbackUri("forward:/fallback/trip-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter().and()
                        )
                        .uri("lb://TRIP-SERVICE")
                )

                .route("reservation-service-route", r -> r
                        .path("/api/reservations/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("trip-service")
                                        .setFallbackUri("forward:/fallback/trip-service")
                                )
                                .tokenRelay()
                        )
                        .uri("lb://TRIP-SERVICE")
                )

                // ==================== PAYMENT SERVICE ====================
                .route("payment-service-route", r -> r
                        .path("/api/payments/**")
                        .and()
                        .method("GET", "POST")
                        .and()
                        .header("X-Request-Type", "internal|external")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("payment-service")
                                        .setFallbackUri("forward:/fallback/payment-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter().and()
                        )
                        .uri("lb://PAYMENT-SERVICE")
                )

                .route("payment-intent-route", r -> r
                        .path("/api/payments/intent/**")
                        .and()
                        .method("POST")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("payment-service")
                                        .setFallbackUri("forward:/fallback/payment-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter().and()
                        )
                        .uri("lb://PAYMENT-SERVICE")
                )

                .route("payment-capture-route", r -> r
                        .path("/api/payments/capture/**")
                        .and()
                        .method("POST")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("payment-service")
                                        .setFallbackUri("forward:/fallback/payment-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter().and()
                        )
                        .uri("lb://PAYMENT-SERVICE")
                )

                .route("payment-refund-route", r -> r
                        .path("/api/payments/refund/**")
                        .and()
                        .method("POST")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("payment-service")
                                        .setFallbackUri("forward:/fallback/payment-service")
                                )
                                .tokenRelay()
                                .requestRateLimiter().and()
                        )
                        .uri("lb://PAYMENT-SERVICE")
                )

                // ==================== NOTIFICATION SERVICE ====================
                .route("notification-service-route", r -> r
                        .path("/api/notifications/**")
                        .and()
                        .method("POST")
                        .and()
                        .header("X-Notification-Type")
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("notification-service")
                                        .setFallbackUri("forward:/fallback/notification-service")
                                )
                                .tokenRelay()
                        )
                        .uri("lb://NOTIFICATION-SERVICE")
                )

                // ==================== TEST ROUTE (para probar timeout/circuit breaker) ====================
                .route("test-route", r -> r
                        .path("/api/test/**")
                        .filters(f -> f
                                .rewritePath("/api/test/(?<segment>.*)", "/test/${segment}")
                                .circuitBreaker(c -> c
                                        .setName("trip-service")
                                        .setFallbackUri("forward:/fallback/trip-service")
                                )
                        )
                        .uri("http://localhost:8080")  // Loopback al mismo gateway
                )

                // ==================== AFTER/BEFORE PREDICATES ====================
                .route("maintenance-route", r -> r
                        .path("/api/maintenance/**")
                        .and()
                        .after(ZonedDateTime.parse("2025-01-01T00:00:00Z"))
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .addResponseHeader("X-Maintenance-Status", "Endpoint de mantenimiento")
                                .tokenRelay()
                        )
                        .uri("lb://TRIP-SERVICE")
                )

                .route("beta-route", r -> r
                        .path("/api/beta/**")
                        .and()
                        .before(ZonedDateTime.parse("2025-12-31T23:59:59Z"))
                        .filters(f -> f
                                .rewritePath("/api/(?<segment>.*)", "/api/v1/${segment}")
                                .addResponseHeader("X-Beta-Status", "Endpoint de beta")
                                .tokenRelay()
                        )
                        .uri("lb://TRIP-SERVICE")
                )

                .build();
    }
}