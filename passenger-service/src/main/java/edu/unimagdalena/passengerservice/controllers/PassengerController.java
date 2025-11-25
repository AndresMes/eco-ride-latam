package edu.unimagdalena.passengerservice.controllers;

import edu.unimagdalena.passengerservice.dtos.requests.PassengerDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.PassengerInfoDtoResponse;
import edu.unimagdalena.passengerservice.services.PassengerService;
import edu.unimagdalena.passengerservice.util.JwtHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER', 'ADMIN')")
    public Mono<ResponseEntity<PassengerInfoDtoResponse>> getMyProfile(Authentication authentication) {
        String keycloakSub = JwtHelper.extractSubject(authentication);

        return passengerService.findPassengerByKeycloakSub(keycloakSub)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/me")
    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER')")
    public Mono<ResponseEntity<PassengerDtoResponse>> createMyProfile(
            @Valid @RequestBody PassengerDtoRequest request,
            Authentication authentication) {

        String keycloakSub = JwtHelper.extractSubject(authentication);

        return passengerService.savePassenger(keycloakSub, request)
                .map(passenger -> ResponseEntity.status(HttpStatus.CREATED).body(passenger));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER')")
    public Mono<ResponseEntity<PassengerDtoResponse>> updateMyProfile(
            @Valid @RequestBody PassengerDtoRequest request,
            Authentication authentication) {

        String keycloakSub = JwtHelper.extractSubject(authentication);

        return passengerService.updatePassenger(keycloakSub, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<PassengerDtoResponse> getAllPassengers() {
        return passengerService.findAll();
    }
}
