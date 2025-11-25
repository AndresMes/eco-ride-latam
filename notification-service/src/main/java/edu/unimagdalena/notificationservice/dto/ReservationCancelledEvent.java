package edu.unimagdalena.notificationservice.dto;

import lombok.Data;

@Data
public class ReservationCancelledEvent {
    private String reservationId;
    private String reason;
}
