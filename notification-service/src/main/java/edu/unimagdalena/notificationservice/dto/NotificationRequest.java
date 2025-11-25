package edu.unimagdalena.notificationservice.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String to;           // email o phone del destinatario
    private String templateCode; // ejemplo: RESERVATION_CONFIRMED
    private Object params;       // datos dinámicos (puede ser Map más adelante)
}
