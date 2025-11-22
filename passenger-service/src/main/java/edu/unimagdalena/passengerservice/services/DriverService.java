package edu.unimagdalena.passengerservice.services;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;

public interface DriverService {
    DriverDtoResponse findDriverById(Long driverId);
    DriverDtoResponse saveDriver(DriverDtoRequest driverDtoRequest);
    DriverDtoResponse updateDriver(Long driverId, DriverDtoUpdateRequest dtoRequest);
    DriverDtoResponse verifyDriver(Long driverId);
}
