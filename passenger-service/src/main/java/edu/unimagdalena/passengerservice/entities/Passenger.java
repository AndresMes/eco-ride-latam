package edu.unimagdalena.passengerservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "passengers")
public class Passenger {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passengerId;

    @Column(name = "keycloak_sub", nullable = false, unique = true)
    private String keycloakSub;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "rating_avg")
    private Double ratingAvg;

    @Column
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "passenger", fetch = FetchType.LAZY)
    private DriverProfile driverProfile;

    @OneToMany(mappedBy = "passenger", fetch = FetchType.LAZY)
    private List<Rating> ratings;
}
