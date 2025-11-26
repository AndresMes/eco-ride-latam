package edu.unimagdalena.notificationservice.config;

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
    @Value("${ecoride.rabbitmq.exchanges.notification}")
    private String notificationExchange;

    @Value("${ecoride.rabbitmq.exchanges.payment}")
    private String paymentExchange;

    // ============ QUEUES ============
    @Value("${ecoride.rabbitmq.queues.notification-reservationConfirmed}")
    private String reservationConfirmedQueue;

    @Value("${ecoride.rabbitmq.queues.notification-reservationCancelled}")
    private String reservationCancelledQueue;

    @Value("${ecoride.rabbitmq.queues.notification-paymentAuthorized}")
    private String paymentAuthorizedQueue;

    @Value("${ecoride.rabbitmq.queues.notification-paymentFailed}")
    private String paymentFailedQueue;

    // ============ ROUTING KEYS ============
    @Value("${ecoride.rabbitmq.routing-keys.notification-reservationConfirmed}")
    private String reservationConfirmedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-reservationCancelled}")
    private String reservationCancelledRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-paymentAuthorized}")
    private String paymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-paymentFailed}")
    private String paymentFailedRoutingKey;

    // ============ MESSAGE CONVERTER ============
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
    public TopicExchange notificationExchange() {
        return new TopicExchange(notificationExchange);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(paymentExchange);
    }

    // ============ QUEUES ============
    @Bean
    public Queue reservationConfirmedQueue() {
        return QueueBuilder.durable(reservationConfirmedQueue)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

    @Bean
    public Queue reservationCancelledQueue() {
        return QueueBuilder.durable(reservationCancelledQueue)
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .build();
    }

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
    public Binding reservationConfirmedBinding() {
        return BindingBuilder
                .bind(reservationConfirmedQueue())
                .to(notificationExchange())
                .with(reservationConfirmedRoutingKey);
    }

    @Bean
    public Binding reservationCancelledBinding() {
        return BindingBuilder
                .bind(reservationCancelledQueue())
                .to(notificationExchange())
                .with(reservationCancelledRoutingKey);
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