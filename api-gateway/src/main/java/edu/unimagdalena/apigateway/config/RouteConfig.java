package edu.unimagdalena.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // ==================== PASSENGER SERVICE ROUTES ====================
                // Passenger profile management
                .route("passenger-service-route", r -> r
                        .path("/api/v1/passengers/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE")
                        .uri("lb://PASSENGER-SERVICE")
                )
                // Driver profile management
                .route("driver-service-route", r -> r
                        .path("/api/v1/drivers/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE", "PATCH")
                        .uri("lb://PASSENGER-SERVICE")
                )
                // Rating system
                .route("ratings-route", r -> r
                        .path("/api/v1/ratings/**")
                        .and()
                        .method("GET", "POST")
                        .uri("lb://PASSENGER-SERVICE")
                )

                // ==================== TRIP SERVICE ROUTES ====================
                .route("trip-service-route", r -> r
                        .path("/api/v1/trips/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE", "PATCH")
                        .uri("lb://TRIP-SERVICE")
                )
                .route("trip-search-route", r -> r
                        .path("/api/v1/trips")
                        .and()
                        .method("GET")
                        .and()
                        .query("origin")
                        .uri("lb://TRIP-SERVICE")
                )
                .route("reservation-service-route", r -> r
                        .path("/api/v1/reservations/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE")
                        .uri("lb://TRIP-SERVICE")
                )

                // ==================== PAYMENT SERVICE ROUTES ====================
                .route("payment-service-route", r -> r
                        .path("/api/v1/payments/**")
                        .and()
                        .method("GET", "POST")
                        .and()
                        .header("X-Request-Type", "internal|external")
                        .uri("lb://PAYMENT-SERVICE")
                )
                .route("payment-intent-route", r -> r
                        .path("/api/v1/payments/intent/**")
                        .and()
                        .method("POST")
                        .uri("lb://PAYMENT-SERVICE")
                )
                .route("payment-capture-route", r -> r
                        .path("/api/v1/payments/capture/**")
                        .and()
                        .method("POST")
                        .uri("lb://PAYMENT-SERVICE")
                )
                .route("payment-refund-route", r -> r
                        .path("/api/v1/payments/refund/**")
                        .and()
                        .method("POST")
                        .uri("lb://PAYMENT-SERVICE")
                )

                // ==================== NOTIFICATION SERVICE ROUTES ====================
                .route("notification-service-route", r -> r
                        .path("/api/v1/notifications/**")
                        .and()
                        .method("POST")
                        .and()
                        .header("X-Notification-Type")
                        .uri("lb://NOTIFICATION-SERVICE")
                )
                .build();
    }
}