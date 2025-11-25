package edu.unimagdalena.passengerservice.entities;

import edu.unimagdalena.passengerservice.enums.VerificationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("driver_profiles")
public class DriverProfile {

    @Id
    @Column("driver_id")
    private Long driverId;

    @Column("license_no")
    private String licenseNo;

    @Column("car_plate")
    private String carPlate;

    @Column("verification_status")
    private VerificationStatus verificationStatus;

    @Column("passenger_id")
    private Long passengerId;

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }
}