package edu.unimagdalena.passengerservice.controllers;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import edu.unimagdalena.passengerservice.services.DriverService;
import edu.unimagdalena.passengerservice.util.JwtHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public Mono<DriverDtoResponse> createDriver(
            @Valid @RequestBody DriverDtoRequest dto,
            Authentication authentication) {

        String keycloakSub = JwtHelper.extractSubject(authentication);
        return driverService.saveDriver(keycloakSub, dto);
    }

    @GetMapping("/{driverId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN', 'PASSENGER')")
    public Mono<ResponseEntity<DriverDtoResponse>> getDriver(@PathVariable Long driverId) {
        return driverService.findDriverById(driverId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{driverId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public Mono<ResponseEntity<DriverDtoResponse>> updateDriver(
            @PathVariable Long driverId,
            @Valid @RequestBody DriverDtoUpdateRequest dto,
            Authentication authentication) {

        String keycloakSub = JwtHelper.extractSubject(authentication);
        return driverService.updateDriver(keycloakSub, driverId, dto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DRIVER')")
    public Mono<ResponseEntity<DriverDtoResponse>> getMyDriverProfile(
            Authentication authentication) {

        String keycloakSub = JwtHelper.extractSubject(authentication);

        return driverService.findDriverByKeycloakSub(keycloakSub)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/{driverId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<DriverDtoResponse> verifyDriver(@PathVariable Long driverId) {
        return driverService.verifyDriver(driverId);
    }
}