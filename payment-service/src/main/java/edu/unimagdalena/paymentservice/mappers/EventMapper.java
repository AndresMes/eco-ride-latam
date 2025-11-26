package edu.unimagdalena.paymentservice.mappers;


import edu.unimagdalena.paymentservice.dtos.responses.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.events.PaymentAuthorizedEvent;
import edu.unimagdalena.paymentservice.events.PaymentFailedEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    PaymentAuthorizedEvent toPaymentAuthorizedEvent(PaymentIntentResponse paymentResponseDTO);

    PaymentFailedEvent toPaymentFailedEvent(PaymentIntentResponse paymentResponseDTO);
}
