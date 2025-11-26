package edu.unimagdalena.notificationservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromEmail;

    /**
     * Envía un email simple
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Envía notificación de reserva confirmada
     */
    public void sendReservationConfirmedEmail(String to, Long reservationId) {
        String subject = "✅ Reserva Confirmada - ECO-RIDE LATAM";
        String body = String.format(
                "Hola,\n\n" +
                        "Tu reserva #%d ha sido confirmada exitosamente.\n\n" +
                        "Detalles:\n" +
                        "- ID de Reserva: %d\n" +
                        "- Estado: CONFIRMADA\n" +
                        "- Fecha: %s\n\n" +
                        "¡Gracias por usar ECO-RIDE LATAM!\n\n" +
                        "Saludos,\n" +
                        "El equipo de ECO-RIDE LATAM",
                reservationId,
                reservationId,
                java.time.LocalDateTime.now().toString()
        );

        sendSimpleEmail(to, subject, body);
    }

    /**
     * Envía notificación de reserva cancelada
     */
    public void sendReservationCancelledEmail(String to, Long reservationId, String reason) {
        String subject = "❌ Reserva Cancelada - ECO-RIDE LATAM";
        String body = String.format(
                "Hola,\n\n" +
                        "Tu reserva #%d ha sido cancelada.\n\n" +
                        "Detalles:\n" +
                        "- ID de Reserva: %d\n" +
                        "- Razón: %s\n" +
                        "- Fecha: %s\n\n" +
                        "Si tienes alguna pregunta, no dudes en contactarnos.\n\n" +
                        "Saludos,\n" +
                        "El equipo de ECO-RIDE LATAM",
                reservationId,
                reservationId,
                reason != null ? reason : "No especificada",
                java.time.LocalDateTime.now().toString()
        );

        sendSimpleEmail(to, subject, body);
    }

    /**
     * Envía notificación de pago autorizado
     */
    public void sendPaymentAuthorizedEmail(String to, Long reservationId, String paymentIntentId) {
        String subject = "💳 Pago Autorizado - ECO-RIDE LATAM";
        String body = String.format(
                "Hola,\n\n" +
                        "Tu pago ha sido autorizado exitosamente.\n\n" +
                        "Detalles:\n" +
                        "- ID de Reserva: %d\n" +
                        "- ID de Pago: %s\n" +
                        "- Fecha: %s\n\n" +
                        "¡Gracias por tu pago!\n\n" +
                        "Saludos,\n" +
                        "El equipo de ECO-RIDE LATAM",
                reservationId,
                paymentIntentId,
                java.time.LocalDateTime.now().toString()
        );

        sendSimpleEmail(to, subject, body);
    }

    /**
     * Envía notificación de pago fallido
     */
    public void sendPaymentFailedEmail(String to, Long reservationId, String reason) {
        String subject = "⚠️ Pago Fallido - ECO-RIDE LATAM";
        String body = String.format(
                "Hola,\n\n" +
                        "Hubo un problema al procesar tu pago.\n\n" +
                        "Detalles:\n" +
                        "- ID de Reserva: %d\n" +
                        "- Razón: %s\n" +
                        "- Fecha: %s\n\n" +
                        "Por favor, intenta nuevamente o contacta a soporte.\n\n" +
                        "Saludos,\n" +
                        "El equipo de ECO-RIDE LATAM",
                reservationId,
                reason != null ? reason : "No especificada",
                java.time.LocalDateTime.now().toString()
        );

        sendSimpleEmail(to, subject, body);
    }
}