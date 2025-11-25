package edu.unimagdalena.passengerservice.services;

import edu.unimagdalena.passengerservice.dtos.requests.PassengerDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerInfoDtoResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PassengerService {

    Mono<PassengerDtoResponse> findPassengerById(Long passengerId);

    Mono<PassengerInfoDtoResponse> findPassengerByKeycloakSub(String keycloakSub);

    Mono<PassengerDtoResponse> savePassenger(String keycloakSub, PassengerDtoRequest dtoRequest);

    Mono<PassengerDtoResponse> updatePassenger(String keycloakSub, PassengerDtoRequest dtoRequest);

    Flux<PassengerDtoResponse> findAll();
}
