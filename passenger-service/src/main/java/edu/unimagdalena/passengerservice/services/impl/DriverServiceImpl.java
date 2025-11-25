package edu.unimagdalena.passengerservice.services.impl;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import edu.unimagdalena.passengerservice.entities.DriverProfile;
import edu.unimagdalena.passengerservice.enums.VerificationStatus;
import edu.unimagdalena.passengerservice.exceptions.*;
import edu.unimagdalena.passengerservice.exceptions.notFound.DriverNotFoundException;
import edu.unimagdalena.passengerservice.exceptions.notFound.PassengerNotFoundException;
import edu.unimagdalena.passengerservice.mappers.DriverMapper;
import edu.unimagdalena.passengerservice.repositories.DriverRepository;
import edu.unimagdalena.passengerservice.repositories.PassengerRepository;
import edu.unimagdalena.passengerservice.services.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final PassengerRepository passengerRepository;

    @Override
    public Mono<DriverDtoResponse> findDriverById(Long driverId) {

        return driverRepository.findById(driverId)
                .switchIfEmpty(Mono.error(
                        new DriverNotFoundException("Driver with ID: " + driverId + " not found")
                ))
                .map(driverMapper::toDriverDto)
                .doOnSuccess(driver -> log.info("Driver found with id: {}", driverId))
                .doOnError(error -> log.error("Error finding driver by id: {}", driverId, error));
    }

    @Override
    public Mono<DriverDtoResponse> saveDriver(String keycloakSub, DriverDtoRequest driverDtoRequest) {

        if (driverDtoRequest == null) {
            return Mono.error(new IllegalArgumentException("Request cannot be null"));
        }

        String licenseNormalized = driverDtoRequest.licenseNo() != null
                ? driverDtoRequest.licenseNo().trim()
                : null;
        String plateNormalized = driverDtoRequest.carPlate() != null
                ? driverDtoRequest.carPlate().trim().toUpperCase()
                : null;

        if (licenseNormalized == null || licenseNormalized.isBlank()) {
            return Mono.error(new IllegalArgumentException("License cannot be blank"));
        }
        if (plateNormalized == null || plateNormalized.isBlank()) {
            return Mono.error(new IllegalArgumentException("Car plate cannot be blank"));
        }

        return passengerRepository.findByKeycloakSub(keycloakSub)
                .switchIfEmpty(Mono.error(
                        new PassengerNotFoundException(
                                "Passenger with keycloak sub: " + keycloakSub + " not found. " +
                                        "Please create a passenger profile first."
                        )
                ))
                .flatMap(passenger ->
                        driverRepository.existsByPassengerId(passenger.getPassengerId())
                                .flatMap(alreadyDriver -> {
                                    if (alreadyDriver) {
                                        return Mono.error(
                                                new AlreadyADriverException(
                                                        "You already have a driver profile"
                                                )
                                        );
                                    }
                                    return Mono.just(passenger);
                                })
                )
                .flatMap(passenger ->
                        driverRepository.existsByLicenseNo(licenseNormalized)
                                .flatMap(licenseExists -> {
                                    if (licenseExists) {
                                        return Mono.error(
                                                new LicenseInUseException("License number already in use")
                                        );
                                    }
                                    return Mono.just(passenger);
                                })
                )
                .flatMap(passenger ->
                        driverRepository.existsByCarPlate(plateNormalized)
                                .flatMap(plateExists -> {
                                    if (plateExists) {
                                        return Mono.error(
                                                new CarPlateInUseException("Car plate already in use")
                                        );
                                    }
                                    return Mono.just(passenger);
                                })
                )
                .flatMap(passenger -> {
                    DriverProfile driver = DriverProfile.builder()
                            .driverId(passenger.getPassengerId())
                            .licenseNo(licenseNormalized)
                            .carPlate(plateNormalized)
                            .passengerId(passenger.getPassengerId())
                            .verificationStatus(VerificationStatus.PENDING_REVIEW)
                            .build();

                    return driverRepository.save(driver);
                })
                .map(driverMapper::toDriverDto)
                .onErrorMap(DataIntegrityViolationException.class, ex -> {
                    return new LicenseInUseException(
                            "Conflict saving driver: license or plate may already be in use"
                    );
                });
    }

    @Override
    public Mono<DriverDtoResponse> updateDriver(
            String keycloakSub,
            Long driverId,
            DriverDtoUpdateRequest dtoRequest) {

        return driverRepository.findById(driverId)
                .switchIfEmpty(Mono.error(
                        new DriverNotFoundException("Driver with ID: " + driverId + " not found")
                ))
                .flatMap(driver ->
                        passengerRepository.findByKeycloakSub(keycloakSub)
                                .switchIfEmpty(Mono.error(
                                        new PassengerNotFoundException(
                                                "Passenger with keycloak sub: " + keycloakSub + " not found"
                                        )
                                ))
                                .flatMap(passenger -> {
                                    if (!driver.getPassengerId().equals(passenger.getPassengerId())) {
                                        return Mono.error(
                                                new UnauthorizedRatingException(
                                                        "You are not authorized to update this driver profile"
                                                )
                                        );
                                    }
                                    return Mono.just(driver);
                                })
                )
                .flatMap(driver -> {
                    String newLicense = dtoRequest.licenseNo() != null
                            ? dtoRequest.licenseNo().trim()
                            : null;
                    String newPlate = dtoRequest.carPlate() != null
                            ? dtoRequest.carPlate().trim().toUpperCase()
                            : null;

                    Mono<Boolean> licenseCheck = Mono.just(false);
                    Mono<Boolean> plateCheck = Mono.just(false);

                    if (newLicense != null && !newLicense.isBlank()
                            && !newLicense.equals(driver.getLicenseNo())) {

                        licenseCheck = driverRepository.existsByLicenseNoAndDriverIdNot(newLicense, driverId)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(
                                                new LicenseInUseException("License number is already in use")
                                        );
                                    }
                                    driver.setLicenseNo(newLicense);
                                    driver.setVerificationStatus(VerificationStatus.PENDING_REVIEW);
                                    return Mono.just(true);
                                });
                    }

                    if (newPlate != null && !newPlate.isBlank()
                            && !newPlate.equals(driver.getCarPlate())) {

                        plateCheck = driverRepository.existsByCarPlateAndDriverIdNot(newPlate, driverId)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(
                                                new CarPlateInUseException("Car plate is already in use")
                                        );
                                    }
                                    driver.setCarPlate(newPlate);
                                    driver.setVerificationStatus(VerificationStatus.PENDING_REVIEW);
                                    return Mono.just(true);
                                });
                    }

                    return Mono.zip(licenseCheck, plateCheck)
                            .flatMap(tuple -> {
                                boolean changed = tuple.getT1() || tuple.getT2();
                                if (!changed) {
                                    log.debug("No changes detected for driver {}", driverId);
                                    return Mono.just(driver);
                                }
                                return driverRepository.save(driver);
                            });
                })
                .map(driverMapper::toDriverDto)
                .onErrorMap(DataIntegrityViolationException.class, ex -> {
                    return new LicenseInUseException(
                            "Conflict saving driver: license or plate may already be in use"
                    );
                });
    }

    @Override
    public Mono<DriverDtoResponse> verifyDriver(Long driverId) {

        return driverRepository.findById(driverId)
                .switchIfEmpty(Mono.error(
                        new DriverNotFoundException("Driver with ID: " + driverId + " not found")
                ))
                .flatMap(driver -> {
                    if (driver.getVerificationStatus() == VerificationStatus.VERIFIED) {
                        return Mono.just(driver);
                    }

                    if (driver.getVerificationStatus() == VerificationStatus.SUSPENDED) {
                        return Mono.error(
                                new DriverNotAllowedForVerifyingException(
                                        "Cannot verify a suspended driver"
                                )
                        );
                    }

                    if (driver.getVerificationStatus() == VerificationStatus.REJECTED) {
                        return Mono.error(
                                new DriverNotAllowedForVerifyingException(
                                        "Cannot verify a rejected driver"
                                )
                        );
                    }

                    driver.setVerificationStatus(VerificationStatus.VERIFIED);
                    return driverRepository.save(driver);
                })
                .map(driverMapper::toDriverDto);
    }

    @Override
    public Mono<DriverDtoResponse> findDriverByKeycloakSub(String keycloakSub) {
        return passengerRepository.findByKeycloakSub(keycloakSub)
                .switchIfEmpty(Mono.error(
                        new PassengerNotFoundException(
                                "Passenger with keycloak sub: " + keycloakSub + " not found"
                        )
                ))
                .flatMap(passenger ->
                        driverRepository.findById(passenger.getPassengerId())
                                .switchIfEmpty(Mono.error(
                                        new DriverNotFoundException(
                                                "Driver profile not found for this user"
                                        )
                                ))
                )
                .map(driverMapper::toDriverDto);
    }
}