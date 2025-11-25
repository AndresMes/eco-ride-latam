package edu.unimagdalena.passengerservice.controllers;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import edu.unimagdalena.passengerservice.services.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('DRIVER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DriverDtoResponse> createDriver(@Valid @RequestBody DriverDtoRequest dto) {
        return driverService.saveDriver(dto);
    }

    @GetMapping("/{driverId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public Mono<DriverDtoResponse> getDriver(@PathVariable Long driverId) {
        return driverService.findDriverById(driverId);
    }

    @PatchMapping("/{driverId}")
    @PreAuthorize("hasAnyRole('DRIVER','ADMIN')")
    public Mono<DriverDtoResponse> updateDriver(@PathVariable Long driverId,
                                                @Valid @RequestBody DriverDtoUpdateRequest dto) {
        return driverService.updateDriver(driverId, dto);
    }

    @PostMapping("/{driverId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<DriverDtoResponse> verifyDriver(@PathVariable Long driverId) {
        return driverService.verifyDriver(driverId);
    }
}