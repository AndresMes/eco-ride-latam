package edu.unimagdalena.paymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ========== EXCHANGES ==========
    @Value("${ecoride.rabbitmq.exchange}")
    private String exchange;

    // ========== QUEUES ==========
    @Value("${ecoride.rabbitmq.queues.reservation-requested}")
    private String reservationRequestedQueue;

    @Value("${ecoride.rabbitmq.queues.payment-authorized}")
    private String paymentAuthorizedQueue;

    @Value("${ecoride.rabbitmq.queues.payment-failed}")
    private String paymentFailedQueue;

    @Value("${ecoride.rabbitmq.queues.notification-reservationConfirmed}")
    private String notificationReservationConfirmedQueue;

    @Value("${ecoride.rabbitmq.queues.notification-reservationCancelled}")
    private String notificationReservationCancelledQueue;

    // ========== ROUTING KEYS ==========
    @Value("${ecoride.rabbitmq.routing-keys.reservation-requested}")
    private String reservationRequestedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-authorized}")
    private String paymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-failed}")
    private String paymentFailedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-reservationConfirmed}")
    private String notificationReservationConfirmedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-reservationCancelled}")
    private String notificationReservationCancelledRoutingKey;

    // ========== EXCHANGE BEAN ==========
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    // ========== QUEUE BEANS ==========
    @Bean
    public Queue reservationRequestedQueue() {
        return QueueBuilder.durable(reservationRequestedQueue)
                .withArgument("x-dead-letter-exchange", exchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", "dlq." + reservationRequestedRoutingKey)
                .build();
    }

    @Bean
    public Queue paymentAuthorizedQueue() {
        return QueueBuilder.durable(paymentAuthorizedQueue).build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable(paymentFailedQueue).build();
    }

    @Bean
    public Queue notificationReservationConfirmedQueue() {
        return QueueBuilder.durable(notificationReservationConfirmedQueue).build();
    }

    @Bean
    public Queue notificationReservationCancelledQueue() {
        return QueueBuilder.durable(notificationReservationCancelledQueue).build();
    }

    // ========== BINDINGS ==========
    @Bean
    public Binding reservationRequestedBinding(Queue reservationRequestedQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(reservationRequestedQueue)
                .to(exchange)
                .with(reservationRequestedRoutingKey);
    }

    @Bean
    public Binding paymentAuthorizedBinding(Queue paymentAuthorizedQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(paymentAuthorizedQueue)
                .to(exchange)
                .with(paymentAuthorizedRoutingKey);
    }

    @Bean
    public Binding paymentFailedBinding(Queue paymentFailedQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(paymentFailedQueue)
                .to(exchange)
                .with(paymentFailedRoutingKey);
    }

    @Bean
    public Binding notificationReservationConfirmedBinding(
            Queue notificationReservationConfirmedQueue,
            TopicExchange exchange) {
        return BindingBuilder
                .bind(notificationReservationConfirmedQueue)
                .to(exchange)
                .with(notificationReservationConfirmedRoutingKey);
    }

    @Bean
    public Binding notificationReservationCancelledBinding(
            Queue notificationReservationCancelledQueue,
            TopicExchange exchange) {
        return BindingBuilder
                .bind(notificationReservationCancelledQueue)
                .to(exchange)
                .with(notificationReservationCancelledRoutingKey);
    }

    // ========== MESSAGE CONVERTER ==========
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ========== RABBIT TEMPLATE ==========
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}