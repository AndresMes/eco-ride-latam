package edu.unimagdalena.tripservice.entities;

import edu.unimagdalena.tripservice.enums.StatusReservation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservations")
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column(nullable = false)
    private Long passengerId;

    @Enumerated(EnumType.STRING)
    private StatusReservation status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id", referencedColumnName = "tripId")
    private Trip trip;

}
