package edu.unimagdalena.tripservice.clients;

import edu.unimagdalena.tripservice.dtos.responses.PassengerDto;
import edu.unimagdalena.tripservice.exceptions.notFound.DriverNotFoundException;
import edu.unimagdalena.tripservice.exceptions.notFound.PassengerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class PassengerClient {

    private static final Logger log = LoggerFactory.getLogger(PassengerClient.class);
    private final WebClient webClient;

    public PassengerClient(WebClient.Builder lbWebClientBuilder) {
        this.webClient = lbWebClientBuilder.build();
    }

    public Mono<PassengerDto> findPassengerById(Long passengerId, @Nullable String authorizationHeader) {
        return webClient.get()
                .uri("http://PASSENGER-SERVICE/api/v1/passengers/me", passengerId)
                .headers(h -> {
                    if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                        h.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
                    }
                })
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new PassengerNotFoundException("Passenger " + passengerId + " not found")))
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new RuntimeException("Passenger service 4xx: " + body))))
                .onStatus(status -> status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new RuntimeException("Passenger service 5xx: " + body))))
                .bodyToMono(PassengerDto.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(ex -> log.error("Error calling passenger-service for id {}: {}", passengerId, ex.toString(), ex));
    }

    public Mono<PassengerDto> findDriverById(Long driverId, @Nullable String authorizationHeader) {
        return webClient.get()
                .uri("http://PASSENGER-SERVICE/api/v1/drivers/me", driverId)
                .headers(h -> {
                    if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                        h.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
                    }
                })
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new PassengerNotFoundException("Passenger " + driverId + " not found")))
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new RuntimeException("Passenger service 4xx: " + body))))
                .onStatus(status -> status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new RuntimeException("Passenger service 5xx: " + body))))
                .bodyToMono(PassengerDto.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(ex -> log.error("Error calling passenger-service for id {}: {}", driverId, ex.toString(), ex));
    }
}