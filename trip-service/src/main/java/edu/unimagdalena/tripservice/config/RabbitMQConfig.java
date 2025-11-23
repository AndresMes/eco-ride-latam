package edu.unimagdalena.tripservice.config;

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

    // ============ EXCHANGES ============
    @Value("${ecoride.rabbitmq.exchanges.reservation}")
    private String reservationExchange;

    @Value("${ecoride.rabbitmq.exchanges.payment}")
    private String paymentExchange;


    // ============ COLAS ============

    @Value("${ecoride.rabbitmq.queues.reservation-requested}")
    private String reservationRequestedQueue;

    @Value("${ecoride.rabbitmq.queues.payment-authorized}")
    private String paymentAuthorizedQueue;

    @Value("${ecoride.rabbitmq.queues.payment-failed}")
    private String paymentFailedQueue;


    // ============ ROUTING KEYS ============
    @Value("${ecoride.rabbitmq.routing-keys.reservation-requested}")
    private String reservationRequestedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-authorized}")
    private String paymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-failed}")
    private String paymentFailedRoutingKey;


    // ============ CONVERTER ============
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // ============ EXCHANGES ============
    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(reservationExchange);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(paymentExchange);
    }

    // ============ QUEUES ============

    // Queue que TripService usa para PUBLICAR eventos de reservación
    @Bean
    public Queue reservationRequestedQueue() {
        return QueueBuilder.durable(reservationRequestedQueue)
                .withArgument("x-dead-letter-exchange", "dlx.exchange") // Dead letter queue
                .build();
    }

    // Queues que TripService usa para ESCUCHAR respuestas del PaymentService
    @Bean
    public Queue paymentAuthorizedQueue() {
        return QueueBuilder.durable(paymentAuthorizedQueue)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable(paymentFailedQueue)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }


    // ============ BINDINGS ============

    @Bean
    public Binding reservationRequestedBinding() {
        return BindingBuilder
                .bind(reservationRequestedQueue())
                .to(reservationExchange())
                .with(reservationRequestedRoutingKey);
    }

    @Bean
    public Binding paymentAuthorizedBinding() {
        return BindingBuilder
                .bind(paymentAuthorizedQueue())
                .to(paymentExchange())
                .with(paymentAuthorizedRoutingKey);
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder
                .bind(paymentFailedQueue())
                .to(paymentExchange())
                .with(paymentFailedRoutingKey);
    }


}
