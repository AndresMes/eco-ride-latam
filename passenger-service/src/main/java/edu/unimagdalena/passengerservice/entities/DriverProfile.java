package edu.unimagdalena.passengerservice.entities;

import edu.unimagdalena.passengerservice.enums.VerificationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

@Getter
@Setter
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

}