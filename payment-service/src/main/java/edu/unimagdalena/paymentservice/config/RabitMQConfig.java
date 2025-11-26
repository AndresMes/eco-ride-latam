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
public class RabitMQConfig {

    @Value("${ecoride.rabbitmq.exchanges.reservation}")
    private String reservationExchange;

    @Value("${ecoride.rabbitmq.exchanges.payment}")
    private String paymentExchange;

    @Value("${ecoride.rabbitmq.queues.reservation-requested}")
    private String reservationRequestedQueue;

    @Value("${ecoride.rabbitmq.queues.payment-authorized}")
    private String paymentAuthorizedQueue;

    @Value("${ecoride.rabbitmq.queues.payment-failed}")
    private String paymentFailedQueue;

    // 🔔 Queues para NotificationService
    @Value("${ecoride.rabbitmq.queues.notification-payment-authorized}")
    private String notificationPaymentAuthorizedQueue;

    @Value("${ecoride.rabbitmq.queues.notification-payment-failed}")
    private String notificationPaymentFailedQueue;

    @Value("${ecoride.rabbitmq.routing-keys.reservation-requested}")
    private String reservationRequestedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-authorized}")
    private String paymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-failed}")
    private String paymentFailedRoutingKey;

    // 🔔 Routing Keys para NotificationService
    @Value("${ecoride.rabbitmq.routing-keys.notification-payment-authorized}")
    private String notificationPaymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-payment-failed}")
    private String notificationPaymentFailedRoutingKey;

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

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(reservationExchange);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(paymentExchange);
    }

    //Escucha de Trip Service
    @Bean
    public Queue reservationRequestedQueue() {
        return QueueBuilder.durable(reservationRequestedQueue)
                .withArgument("x-dead-letter-exchange", "dlx.exchange") // Dead letter queue
                .build();
    }

    // Queues que Payment publica a TripService
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

    // 🔔 Queues que Payment publica a NotificationService
    @Bean
    public Queue notificationPaymentAuthorizedQueue() {
        return QueueBuilder.durable(notificationPaymentAuthorizedQueue)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

    @Bean
    public Queue notificationPaymentFailedQueue() {
        return QueueBuilder.durable(notificationPaymentFailedQueue)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

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

    // 🔔 Bindings para NotificationService
    @Bean
    public Binding notificationPaymentAuthorizedBinding() {
        return BindingBuilder
                .bind(notificationPaymentAuthorizedQueue())
                .to(paymentExchange())
                .with(notificationPaymentAuthorizedRoutingKey);
    }

    @Bean
    public Binding notificationPaymentFailedBinding() {
        return BindingBuilder
                .bind(notificationPaymentFailedQueue())
                .to(paymentExchange())
                .with(notificationPaymentFailedRoutingKey);
    }
}