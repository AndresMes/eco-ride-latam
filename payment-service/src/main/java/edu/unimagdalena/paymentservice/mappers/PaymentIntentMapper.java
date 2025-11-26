package edu.unimagdalena.paymentservice.mappers;

import edu.unimagdalena.paymentservice.dtos.responses.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.entities.PaymentIntent;
import edu.unimagdalena.paymentservice.events.ReservationRequestedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentIntentMapper {

    PaymentIntent ReservationRequestedEventToPaymentIntent(ReservationRequestedEvent event);
    PaymentIntentResponse paymentIntentToPaymentResponseDTO(PaymentIntent paymentIntent);
}
