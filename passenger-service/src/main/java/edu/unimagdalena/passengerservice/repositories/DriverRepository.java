package edu.unimagdalena.passengerservice.repositories;

import edu.unimagdalena.passengerservice.entities.DriverProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface DriverRepository extends R2dbcRepository<DriverProfile, Long> {

    @Query("SELECT EXISTS(SELECT 1 FROM driver_profiles WHERE passenger_id = :passengerId)")
    Mono<Boolean> existsByPassengerId(Long passengerId);

    @Query("SELECT EXISTS(SELECT 1 FROM driver_profiles WHERE license_no = :licenseNo)")
    Mono<Boolean> existsByLicenseNo(String licenseNo);

    @Query("SELECT EXISTS(SELECT 1 FROM driver_profiles WHERE license_no = :licenseNo AND driver_id != :driverId)")
    Mono<Boolean> existsByLicenseNoAndDriverIdNot(String licenseNo, Long driverId);

    @Query("SELECT EXISTS(SELECT 1 FROM driver_profiles WHERE car_plate = :carPlate AND driver_id != :driverId)")
    Mono<Boolean> existsByCarPlateAndDriverIdNot(String carPlate, Long driverId);

    @Query("SELECT EXISTS(SELECT 1 FROM driver_profiles WHERE car_plate = :carPlate)")
    Mono<Boolean> existsByCarPlate(String carPlate);
}