package edu.unimagdalena.notificationservice.controller;

import edu.unimagdalena.notificationservice.dto.NotificationRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @PostMapping
    public String sendNotification(@RequestBody NotificationRequest request) {
        System.out.println("=== NOTIFICACIÓN RECIBIDA ===");
        System.out.println("To: " + request.getTo());
        System.out.println("Template: " + request.getTemplateCode());
        System.out.println("Params: " + request.getParams());
        System.out.println("=============================");
        return "Notificación recibida correctamente";
    }
}
