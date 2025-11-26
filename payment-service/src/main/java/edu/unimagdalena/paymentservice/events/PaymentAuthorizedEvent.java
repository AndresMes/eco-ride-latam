package edu.unimagdalena.paymentservice.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAuthorizedEvent {

    private Long reservationId;
    private String paymentIntentId;
    private String chargeId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String eventId;
}
