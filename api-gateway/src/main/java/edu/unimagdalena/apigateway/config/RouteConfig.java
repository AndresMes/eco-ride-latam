package edu.unimagdalena.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Route Configuration for Eco-Ride LATAM Microservices
 *
 * Configures routing rules with predicates and filters for:
 * - TripService: Trip management and reservations
 * - PassengerService: Passenger profiles and driver profiles
 * - PaymentService: Payment processing and transactions
 * - NotificationService: Email/SMS/Push notifications
 *
 * TokenRelay Filter:
 * - Automatically relays OAuth2 access tokens to downstream services
 * - Supports both user tokens (authorization_code) and service tokens (client_credentials)
 * - Configured globally in application.yaml: default-filters: TokenRelay
 */
@Configuration
public class RouteConfig {

    /**
     * Configure routes with predicates and filters
     *
     * Predicates used:
     * - Path: Route based on request path
     * - Method: Route based on HTTP method
     * - Header: Route based on request headers
     * - Query: Route based on query parameters
     *
     * Load balancing: Uses Eureka service discovery with lb:// protocol
     *
     * TokenRelay: Configured globally, automatically adds Authorization header
     * with access token to all downstream requests
     */
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // ==================== TRIP SERVICE ROUTES ====================
                // All trip operations - CRUD endpoints
                .route("trip-service-route", r -> r
                        .path("/api/v1/trips/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE", "PATCH")
                        .uri("lb://TRIP-SERVICE")
                )
                // Trip search with query parameters
                .route("trip-search-route", r -> r
                        .path("/api/v1/trips")
                        .and()
                        .method("GET")
                        .and()
                        .query("origin")
                        .uri("lb://TRIP-SERVICE")
                )

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
                        .method("GET", "POST", "PUT", "DELETE")
                        .uri("lb://PASSENGER-SERVICE")
                )
                // Rating system
                .route("ratings-route", r -> r
                        .path("/api/v1/ratings/**")
                        .and()
                        .method("GET", "POST")
                        .uri("lb://PASSENGER-SERVICE")
                )

                // ==================== PAYMENT SERVICE ROUTES ====================
                // General payment operations
                .route("payment-service-route", r -> r
                        .path("/api/v1/payments/**")
                        .and()
                        .method("GET", "POST")
                        .and()
                        .header("X-Request-Type", "internal|external")
                        .uri("lb://PAYMENT-SERVICE")
                )
                // Payment intent creation
                .route("payment-intent-route", r -> r
                        .path("/api/v1/payments/intent/**")
                        .and()
                        .method("POST")
                        .uri("lb://PAYMENT-SERVICE")
                )
                // Payment capture
                .route("payment-capture-route", r -> r
                        .path("/api/v1/payments/capture/**")
                        .and()
                        .method("POST")
                        .uri("lb://PAYMENT-SERVICE")
                )
                // Payment refund
                .route("payment-refund-route", r -> r
                        .path("/api/v1/payments/refund/**")
                        .and()
                        .method("POST")
                        .uri("lb://PAYMENT-SERVICE")
                )

                // ==================== NOTIFICATION SERVICE ROUTES ====================
                // Notification sending (requires notification type header)
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