package edu.unimagdalena.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String eventType; // ejemplo: RESERVATION_CONFIRMED
    private Object payload;   // contenido del evento (JSON o DTO)
    private String timestamp; // ISO-8601 opcional
}
