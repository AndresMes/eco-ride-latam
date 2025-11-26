package edu.unimagdalena.tripservice.entities;

import edu.unimagdalena.tripservice.enums.StatusTrip;
import edu.unimagdalena.tripservice.exceptions.TripCancelledOrFinishedException;
import edu.unimagdalena.tripservice.exceptions.TripFullException;
import edu.unimagdalena.tripservice.exceptions.TripInProgressException;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("trips")
public class Trip {

    @Id
    @Column("trip_id")
    private Long tripId;

    @Column("driver_id")
    private Long driverId;

    @Column("origin")
    private String origin;

    @Column("destination")
    private String destination;

    @Column("start_time")
    private LocalDateTime startTime;

    @Column("seats_available")
    private Long seatsAvailable;

    @Column(value = "price")
    private BigDecimal price;

    @Column("status")
    private StatusTrip status;

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

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Long getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(Long seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public StatusTrip getStatus() {
        return status;
    }

    public void setStatus(StatusTrip status) {
        this.status = status;
    }
}
