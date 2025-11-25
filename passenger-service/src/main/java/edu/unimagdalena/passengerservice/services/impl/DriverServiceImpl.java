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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final PassengerRepository passengerRepository;

    public DriverServiceImpl(DriverRepository driverRepository, DriverMapper driverMapper, PassengerRepository passengerRepository) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
        this.passengerRepository = passengerRepository;
    }

    @Override
    public Mono<DriverDtoResponse> findDriverById(Long driverId) {
        return driverRepository.findById(driverId)
                .switchIfEmpty(Mono.error(new DriverNotFoundException("Driver with ID: " + driverId + " not found")))
                .map(driverMapper::toDriverDto);
    }

    @Override
    public Mono<DriverDtoResponse> saveDriver(DriverDtoRequest driverDtoRequest) {
        if (driverDtoRequest == null) {
            return Mono.error(new IllegalArgumentException("Request is null"));
        }

        String licenseNormalized = driverDtoRequest.licenseNo() != null
                ? driverDtoRequest.licenseNo().trim()
                : null;
        String plateNormalized = driverDtoRequest.carPlate() != null
                ? driverDtoRequest.carPlate().trim().toUpperCase()
                : null;

        if (licenseNormalized == null || licenseNormalized.isBlank()) {
            return Mono.error(new IllegalArgumentException("license cannot be blank"));
        }
        if (plateNormalized == null || plateNormalized.isBlank()) {
            return Mono.error(new IllegalArgumentException("car plate cannot be blank"));
        }

        return passengerRepository.findById(driverDtoRequest.passengerId())
                .switchIfEmpty(Mono.error(new PassengerNotFoundException(
                        "Passenger with ID: " + driverDtoRequest.passengerId() + " not found")))
                .flatMap(passenger ->
                        driverRepository.existsByPassengerId(passenger.getPassengerId())
                                .flatMap(alreadyDriver -> {
                                    if (alreadyDriver) {
                                        return Mono.error(new AlreadyADriverException("Passenger already has a driver profile"));
                                    }
                                    return Mono.just(passenger);
                                })
                )
                .flatMap(passenger ->
                        driverRepository.existsByLicenseNo(licenseNormalized)
                                .flatMap(licenseExists -> {
                                    if (licenseExists) {
                                        return Mono.error(new LicenseInUseException("License number already in use"));
                                    }
                                    return Mono.just(passenger);
                                })
                )
                .flatMap(passenger ->
                        driverRepository.existsByCarPlate(plateNormalized)
                                .flatMap(plateExists -> {
                                    if (plateExists) {
                                        return Mono.error(new CarPlateInUseException("Car plate already in use"));
                                    }
                                    return Mono.just(passenger);
                                })
                )
                .flatMap(passenger -> {
                    DriverProfile driver = driverMapper.toEntity(driverDtoRequest);
                    driver.setLicenseNo(licenseNormalized);
                    driver.setCarPlate(plateNormalized);
                    driver.setPassengerId(passenger.getPassengerId());
                    driver.setVerificationStatus(VerificationStatus.PENDING_REVIEW);

                    if (driver.getDriverId() == null && passenger.getPassengerId() != null) {
                        driver.setDriverId(passenger.getPassengerId());
                    }

                    return driverRepository.save(driver);
                })
                .map(driverMapper::toDriverDto)
                .onErrorMap(DataIntegrityViolationException.class,
                        ex -> new LicenseInUseException("Conflict saving driver: license or plate may already be in use"));
    }

    @Override
    public Mono<DriverDtoResponse> updateDriver(Long driverId, DriverDtoUpdateRequest dtoRequest) {
        return driverRepository.findById(driverId)
                .switchIfEmpty(Mono.error(new DriverNotFoundException("Driver with ID: " + driverId + " not found")))
                .flatMap(driver -> {
                    String newLicense = dtoRequest.licenseNo() != null ? dtoRequest.licenseNo().trim() : null;
                    String newPlate = dtoRequest.carPlate() != null ? dtoRequest.carPlate().trim().toUpperCase() : null;

                    Mono<Boolean> licenseCheck = Mono.just(false);
                    Mono<Boolean> plateCheck = Mono.just(false);

                    if (newLicense != null && !newLicense.isBlank() && !newLicense.equals(driver.getLicenseNo())) {
                        licenseCheck = driverRepository.existsByLicenseNoAndDriverIdNot(newLicense, driverId)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new LicenseInUseException("License number is already in use"));
                                    }
                                    driver.setLicenseNo(newLicense);
                                    driver.setVerificationStatus(VerificationStatus.PENDING_REVIEW);
                                    return Mono.just(true);
                                });
                    }

                    if (newPlate != null && !newPlate.isBlank() && !newPlate.equals(driver.getCarPlate())) {
                        plateCheck = driverRepository.existsByCarPlateAndDriverIdNot(newPlate, driverId)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new CarPlateInUseException("Car plate is already in use"));
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
                                    return Mono.just(driver);
                                }
                                return driverRepository.save(driver);
                            });
                })
                .map(driverMapper::toDriverDto)
                .onErrorMap(DataIntegrityViolationException.class,
                        ex -> new LicenseInUseException("Conflict saving driver: license or plate may already be in use"));
    }

    @Override
    public Mono<DriverDtoResponse> verifyDriver(Long driverId) {
        return driverRepository.findById(driverId)
                .switchIfEmpty(Mono.error(new DriverNotFoundException("Driver with ID: " + driverId + " not found")))
                .flatMap(driver -> {
                    if (driver.getVerificationStatus() == VerificationStatus.VERIFIED) {
                        return Mono.just(driver);
                    }

                    if (driver.getVerificationStatus().equals(VerificationStatus.SUSPENDED)) {
                        return Mono.error(new DriverNotAllowedForVerifyingException("Cannot verified a suspended driver"));
                    }

                    if (driver.getVerificationStatus().equals(VerificationStatus.REJECTED)) {
                        return Mono.error(new DriverNotAllowedForVerifyingException("Cannot verified a rejected driver"));
                    }

                    driver.setVerificationStatus(VerificationStatus.VERIFIED);
                    return driverRepository.save(driver);
                })
                .map(driverMapper::toDriverDto);
    }
}