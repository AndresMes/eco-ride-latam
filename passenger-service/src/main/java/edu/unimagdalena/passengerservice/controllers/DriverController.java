package edu.unimagdalena.passengerservice.controllers;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import edu.unimagdalena.passengerservice.services.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/profile")
    public ResponseEntity<DriverDtoResponse> createDriver(@Valid @RequestBody DriverDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.saveDriver(dto));
    }

    //Cambiar por "me" cuando haya Keycloak
    @GetMapping("/{driverId}")
    public ResponseEntity<DriverDtoResponse> getDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverService.findDriverById(driverId));
    }

    @PatchMapping("/{driverId}")
    public ResponseEntity<DriverDtoResponse> updateDriver(@PathVariable Long driverId,
                                                          @Valid @RequestBody DriverDtoUpdateRequest dto) {
        return ResponseEntity.ok(driverService.updateDriver(driverId, dto));
    }

    //Solo se mantendrá si se añade rol ADMIN
    @PostMapping("/{driverId}/verify")
    public ResponseEntity<DriverDtoResponse> verifyDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverService.verifyDriver(driverId));
    }
}
