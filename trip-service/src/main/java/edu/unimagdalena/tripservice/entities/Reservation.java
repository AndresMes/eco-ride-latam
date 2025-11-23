package edu.unimagdalena.tripservice.entities;

import edu.unimagdalena.tripservice.enums.StatusReservation;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("reservations")
public class Reservation {

    @Id
    private Long reservationId;

    @Column("passenger_id")
    private Long passengerId;

    /**
     * Almacenamos el enum como String en la columna "status".
     * Si tienes problemas, podemos añadir converters personalizados.
     */
    @Column("status")
    private StatusReservation status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("trip_id")
    private Long tripId;

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public StatusReservation getStatus() {
        return status;
    }

    public void setStatus(StatusReservation status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }
}
