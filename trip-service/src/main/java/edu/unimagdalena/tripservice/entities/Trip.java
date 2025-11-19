package edu.unimagdalena.tripservice.entities;

import edu.unimagdalena.tripservice.enums.StatusTrip;
import edu.unimagdalena.tripservice.exceptions.TripCancelledOrFinishedException;
import edu.unimagdalena.tripservice.exceptions.TripFullException;
import edu.unimagdalena.tripservice.exceptions.TripInProgressException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public void reserveSeat() {
        if (status == StatusTrip.CANCELLED || status == StatusTrip.FINISHED) {
            throw new TripCancelledOrFinishedException("Trip not available for reservations");
        }
        if (status == StatusTrip.FULL) {
            throw new TripFullException("Trip is already full");
        }
        if (status == StatusTrip.IN_PROGRESS) {
            throw new TripInProgressException("Trip is already in progress");
        }
        if (seatsAvailable == null || seatsAvailable <= 0) {
            throw new TripFullException("No seats available");
        }

        seatsAvailable = seatsAvailable - 1;
        if (seatsAvailable == 0) {
            status = StatusTrip.FULL;
        }
    }

    public void restoreSeat() {
        if (seatsAvailable == null) seatsAvailable = 0L;
        seatsAvailable = seatsAvailable + 1;
        if (status == StatusTrip.FULL) {
            status = StatusTrip.SCHEDULED;
        }
    }
}
