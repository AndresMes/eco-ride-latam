package edu.unimagdalena.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Route Configuration for Eco-Ride LATAM Microservices
 *
 * Configures routing rules with predicates for:
 * - TripService: Trip management and reservations
 * - PassengerService: Passenger profiles and driver profiles
 * - PaymentService: Payment processing and transactions
 * - NotificationService: Email/SMS/Push notifications
 */
@Configuration
public class RouteConfig {
    /*** Configure routes with predicates and filters
     * Predicates used:
     * - Path: Route based on request path
     * - Method: Route based on HTTP method
     * - Header: Route based on request headers
     * Load balancing: Uses Eureka service discovery with lb:// protocol
     */

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            //Trip Service Routes
                .route("trip-service-route", r -> r
                        .path("/api/v1/trips/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE", "PATCH")
                        .uri("lb://TRIP-SERVICE")
                )
                .route("trip-service-route", r -> r
                        .path("/api/v1/trips")
                        .and()
                        .method("GET")
                        .and()
                        .query("origin")
                        .uri("lb://TRIP-SERVICE")
                )
            //Passenger Service Routes
                .route("passenger-service-route", r -> r
                        .path("/api/v1/passengers/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE")
                        .uri("lb://PASSENGER-SERVICE")
                )
                .route("driver-service-route", r -> r
                        .path("/api/v1/drivers/**")
                        .and()
                        .method("GET", "POST", "PUT", "DELETE")
                        .uri("lb://PASSENGER-SERVICE")
                )
                .route("ratings-route", r -> r
                        .path("/api/v1/ratings/**")
                        .and()
                        .method("GET", "POST")
                        .uri("lb://PASSENGER-SERVICE")
                )
            //Payment Service Routes
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
                        .uri("lb://PAYMENT-SERVICE"))
                .route("payment-refund-route", r -> r
                        .path("/api/v1/payments/refund/**")
                        .and()
                        .method("POST")
                        .uri("lb://PAYMENT-SERVICE")
                )
            //Notification Service Routes
                .route("notification-service-route", r -> r
                        .path("/api/v1/notifications/**")
                        .and()
                        .method("POST")
                        .and()
                        .header("X-Notification-Type")
                        .uri("lb://NOTIFICATION-SERVICE"))
                .build();
    }
}
