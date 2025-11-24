package edu.unimagdalena.passengerservice.controllers;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import edu.unimagdalena.passengerservice.services.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/profile")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DriverDtoResponse> createDriver(@Valid @RequestBody DriverDtoRequest dto) {
        return driverService.saveDriver(dto);
    }

    @GetMapping("/{driverId}")
    public Mono<DriverDtoResponse> getDriver(@PathVariable Long driverId) {
        return driverService.findDriverById(driverId);
    }

    @PatchMapping("/{driverId}")
    public Mono<DriverDtoResponse> updateDriver(@PathVariable Long driverId,
                                                @Valid @RequestBody DriverDtoUpdateRequest dto) {
        return driverService.updateDriver(driverId, dto);
    }

    @PostMapping("/{driverId}/verify")
    public Mono<DriverDtoResponse> verifyDriver(@PathVariable Long driverId) {
        return driverService.verifyDriver(driverId);
    }
}