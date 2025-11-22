package edu.unimagdalena.passengerservice.entities;

import edu.unimagdalena.passengerservice.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "driver_profiles")
public class DriverProfile {

    @Id
    private Long driverId;

    @Column(nullable = false, unique = true)
    private String licenseNo;

    @Column(nullable = false)
    private String carPlate;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "passenger_id", referencedColumnName = "passengerId")
    private Passenger passenger;

    @OneToMany(mappedBy = "to", fetch = FetchType.LAZY)
    private List<Rating> ratings;
}
