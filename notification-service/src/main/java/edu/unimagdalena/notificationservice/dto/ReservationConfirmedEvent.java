package edu.unimagdalena.notificationservice.dto;

import lombok.Data;

@Data
public class ReservationConfirmedEvent {
    private String reservationId;
}
