package edu.unimagdalena.notificationservice.dto;

import lombok.Data;

@Data
public class TripCompletedEvent {
    private String tripId;
}
