package edu.unimagdalena.passengerservice.repositories;

import edu.unimagdalena.passengerservice.entities.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<DriverProfile, Long> {

    boolean existsByPassenger_PassengerId(Long passengerId);
    boolean existsByLicenseNo(String licenseNo);
    boolean existsByLicenseNoAndDriverIdNot(String licenseNo, Long driverId);
    boolean existsByCarPlateAndDriverIdNot(String carPlate, Long driverId);
    boolean existsByCarPlate(String carPlate);

}
