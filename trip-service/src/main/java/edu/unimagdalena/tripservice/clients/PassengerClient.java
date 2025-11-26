package edu.unimagdalena.tripservice.clients;

import edu.unimagdalena.tripservice.dtos.responses.PassengerDto;
import edu.unimagdalena.tripservice.exceptions.notFound.PassengerNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class PassengerClient {

    private final WebClient webClient;

    public PassengerClient(WebClient.Builder lbWebClientBuilder) {
        this.webClient = lbWebClientBuilder.build();
    }

    public Mono<PassengerDto> findPassengerById(String passengerId, @Nullable String authorizationHeader) {
        return webClient.get()
                .uri("http://passenger-service/passengers/{id}", passengerId)
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
                .timeout(java.time.Duration.ofSeconds(3));
    }

    private Mono<Void> handleResponse(ClientResponse response, String passengerId) {
        if (response.statusCode().is2xxSuccessful()) {
            return Mono.empty();
        } else if (response.statusCode().value() == 404) {
            return Mono.error(new edu.unimagdalena.tripservice.exceptions.notFound.PassengerNotFoundException("Passenger " + passengerId + " not found"));
        } else if (response.statusCode().is4xxClientError()) {
            return response.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> Mono.error(new RuntimeException("Passenger service 4xx: " + body)));
        } else {
            return response.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> Mono.error(new RuntimeException("Passenger service error: " + response.statusCode() + " body:" + body)));
        }
    }
}