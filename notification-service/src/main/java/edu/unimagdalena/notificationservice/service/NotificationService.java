package edu.unimagdalena.notificationservice.service;

import edu.unimagdalena.notificationservice.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void sendNotification(NotificationRequest request) {

        log.info("📩 Processing notification...");
        log.info("➡️ To: {}", request.getTo());
        log.info("➡️ Template: {}", request.getTemplateCode());
        log.info("➡️ Params: {}", request.getParams());

        // Simulación mínima obligatoria
        log.info("✅ Notification sent successfully (mock)");
    }
}
