package edu.unimagdalena.passengerservice.services.impl;

import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoRequest;
import edu.unimagdalena.passengerservice.dtos.requests.DriverDtoUpdateRequest;
import edu.unimagdalena.passengerservice.dtos.responses.DriverDtoResponse;
import edu.unimagdalena.passengerservice.entities.DriverProfile;
import edu.unimagdalena.passengerservice.entities.Passenger;
import edu.unimagdalena.passengerservice.enums.VerificationStatus;
import edu.unimagdalena.passengerservice.exceptions.*;
import edu.unimagdalena.passengerservice.exceptions.notFound.DriverNotFoundException;
import edu.unimagdalena.passengerservice.exceptions.notFound.PassengerNotFoundException;
import edu.unimagdalena.passengerservice.mappers.DriverMapper;
import edu.unimagdalena.passengerservice.repositories.DriverRepository;
import edu.unimagdalena.passengerservice.repositories.PassengerRepository;
import edu.unimagdalena.passengerservice.services.DriverService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final PassengerRepository passengerRepository;

    @Override
    public DriverDtoResponse findDriverById(Long driverId) {
        DriverProfile driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver with ID: " + driverId + " not found"));

        return driverMapper.toDriverDto(driver);
    }


    //HACER VALIDACIONES CON KEYCLOAK SUB
    @Override
    @Transactional
    public DriverDtoResponse saveDriver(DriverDtoRequest driverDtoRequest) {

        if (driverDtoRequest == null) throw new IllegalArgumentException("Request is null");

        String licenseNormalized = driverDtoRequest.licenseNo() != null
                ? driverDtoRequest.licenseNo().trim()
                : null;
        String plateNormalized = driverDtoRequest.carPlate() != null
                ? driverDtoRequest.carPlate().trim().toUpperCase()
                : null;

        Passenger passenger = passengerRepository.findById(driverDtoRequest.passengerId())
                .orElseThrow(() -> new PassengerNotFoundException("Passenger with ID: " + driverDtoRequest.passengerId() + " not found"));

        boolean alreadyDriver = driverRepository.existsByPassenger_PassengerId(passenger.getPassengerId());
        if (alreadyDriver) {
            throw new AlreadyADriverException("Passenger already has a driver profile");
        }

        if (licenseNormalized == null || licenseNormalized.isBlank()) {
            throw new IllegalArgumentException("license cannot be blank");
        }
        if (plateNormalized == null || plateNormalized.isBlank()) {
            throw new IllegalArgumentException("car plate cannot be blank");
        }

        if (driverRepository.existsByLicenseNo(licenseNormalized)) {
            throw new LicenseInUseException("License number already in use");
        }

        if (driverRepository.existsByCarPlate(plateNormalized)) {
            throw new CarPlateInUseException("Car plate already in use");
        }

        DriverProfile driver = driverMapper.toEntity(driverDtoRequest);
        driver.setLicenseNo(licenseNormalized);
        driver.setCarPlate(plateNormalized);

        driver.setPassenger(passenger);
        driver.setVerificationStatus(VerificationStatus.PENDING_REVIEW);

        if (driver.getDriverId() == null && passenger.getPassengerId() != null) {
            driver.setDriverId(passenger.getPassengerId());
        }

        try {
            DriverProfile saved = driverRepository.save(driver);
            // opcional si queda tiempo: publicar evento DriverProfileCreated(saved.getDriverId())
            return driverMapper.toDriverDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new LicenseInUseException("Conflict saving driver: license or plate may already be in use");
        }
    }

    @Override
    @Transactional
    public DriverDtoResponse updateDriver(Long driverId, DriverDtoUpdateRequest dtoRequest) {
        DriverProfile driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver with ID: " + driverId + " not found"));

        boolean changed = false;

        String newLicense = dtoRequest.licenseNo() != null ? dtoRequest.licenseNo().trim() : null;
        String newPlate   = dtoRequest.carPlate()  != null ? dtoRequest.carPlate().trim().toUpperCase() : null;

        if (newLicense != null && !newLicense.isBlank()) {
            if (driverRepository.existsByLicenseNoAndDriverIdNot(newLicense, driverId)) {
                throw new LicenseInUseException("License number is already in use");
            }
            if (!newLicense.equals(driver.getLicenseNo())) {
                driver.setLicenseNo(newLicense);
                driver.setVerificationStatus(VerificationStatus.PENDING_REVIEW);
                changed = true;
            }
        }

        if (newPlate != null && !newPlate.isBlank()) {
            if (driverRepository.existsByCarPlateAndDriverIdNot(newPlate, driverId)) {
                throw new CarPlateInUseException("Car plate is already in use");
            }
            if (!newPlate.equals(driver.getCarPlate())) {
                driver.setCarPlate(newPlate);
                driver.setVerificationStatus(VerificationStatus.PENDING_REVIEW);
                changed = true;
            }
        }

        if (!changed) {
            return driverMapper.toDriverDto(driver);
        }

        try {
            DriverProfile saved = driverRepository.save(driver);
            return driverMapper.toDriverDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new LicenseInUseException("Conflict saving driver: license or plate may already be in use");
        }

    }

    @Override
    @Transactional
    public DriverDtoResponse verifyDriver(Long driverId) {
        DriverProfile driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver with ID: " + driverId + " not found"));

        if (driver.getVerificationStatus() == VerificationStatus.VERIFIED) {
            return driverMapper.toDriverDto(driver);
        }

        if(driver.getVerificationStatus().equals(VerificationStatus.SUSPENDED)){
            throw new DriverNotAllowedForVerifyingException("Cannot verified a suspended driver");
        }

        if(driver.getVerificationStatus().equals(VerificationStatus.REJECTED)){
            throw new DriverNotAllowedForVerifyingException("Cannot verified a rejected driver");
        }

        driver.setVerificationStatus(VerificationStatus.VERIFIED);
        return driverMapper.toDriverDto(driverRepository.save(driver));
    }
}
