package edu.unimagdalena.tripservice.entities;

import edu.unimagdalena.tripservice.enums.StatusTrip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trips")
public class Trip {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @Column
    private Long driverId;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private Long seatsAvailable;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private StatusTrip status;

    @OneToMany(mappedBy = "trip")
    private List<Reservation> reservations;
}
