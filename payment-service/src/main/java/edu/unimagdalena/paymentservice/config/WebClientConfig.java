package edu.unimagdalena.paymentservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * WebClient con LoadBalancer para comunicación entre microservicios
     * Usa Eureka para descubrimiento de servicios
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    /**
     * WebClient específico para TripService
     */
    @Bean
    public WebClient tripServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://trip-service") // Nombre del servicio en Eureka
                .build();
    }

    /**
     * WebClient específico para NotificationService
     */
    @Bean
    public WebClient notificationServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://notification-service") // Nombre del servicio en Eureka
                .build();
    }
}