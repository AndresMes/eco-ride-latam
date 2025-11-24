package edu.unimagdalena.passengerservice.services;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import reactor.core.publisher.Mono;

public interface DriverService {
    Mono<DriverDtoResponse> findDriverById(Long driverId);
    Mono<DriverDtoResponse> saveDriver(DriverDtoRequest driverDtoRequest);
    Mono<DriverDtoResponse> updateDriver(Long driverId, DriverDtoUpdateRequest dtoRequest);
    Mono<DriverDtoResponse> verifyDriver(Long driverId);
}