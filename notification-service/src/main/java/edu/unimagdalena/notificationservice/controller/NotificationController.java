package edu.unimagdalena.notificationservice.controller;

import edu.unimagdalena.notificationservice.dto.NotificationRequest;
import edu.unimagdalena.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {

        notificationService.sendNotification(request);

        return ResponseEntity.ok("✅ Notification processed successfully");
    }
}
