package edu.unimagdalena.passengerservice.services;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import reactor.core.publisher.Mono;

public interface DriverService {

    Mono<DriverDtoResponse> findDriverById(Long driverId);
    Mono<DriverDtoResponse> saveDriver(String keycloakSub, DriverDtoRequest driverDtoRequest);
    Mono<DriverDtoResponse> updateDriver(String keycloakSub, Long driverId, DriverDtoUpdateRequest dtoRequest);
    Mono<DriverDtoResponse> verifyDriver(Long driverId);
    Mono<DriverDtoResponse> findDriverByKeycloakSub(String keycloakSub);
}