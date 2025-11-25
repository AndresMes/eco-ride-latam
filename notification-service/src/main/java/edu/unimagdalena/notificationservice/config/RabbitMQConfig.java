package edu.unimagdalena.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nombre del exchange
    @Value("${ecoride.rabbitmq.exchange:ecoride-exchange}")
    private String exchangeName;

    // Colas
    @Value("${ecoride.rabbitmq.queues.trip-completed}")
    private String tripCompletedQueue;

    @Value("${ecoride.rabbitmq.queues.notification-reservationConfirmed}")
    private String reservationConfirmedQueue;

    @Value("${ecoride.rabbitmq.queues.notification-reservationCancelled}")
    private String reservationCancelledQueue;

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue tripCompletedQueue() {
        return new Queue(tripCompletedQueue);
    }

    @Bean
    public Queue reservationConfirmedQueue() {
        return new Queue(reservationConfirmedQueue);
    }

    @Bean
    public Queue reservationCancelledQueue() {
        return new Queue(reservationCancelledQueue);
    }

    // Bindings: si quieres que la cola reciba todo lo que llegue al exchange con la misma routing key
    @Bean
    public Binding bindingTripCompleted(Queue tripCompletedQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(tripCompletedQueue)
                .to(appExchange)
                .with(tripCompletedQueue.getName());
    }

    @Bean
    public Binding bindingReservationConfirmed(Queue reservationConfirmedQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(reservationConfirmedQueue)
                .to(appExchange)
                .with(reservationConfirmedQueue.getName());
    }

    @Bean
    public Binding bindingReservationCancelled(Queue reservationCancelledQueue, TopicExchange appExchange) {
        return BindingBuilder.bind(reservationCancelledQueue)
                .to(appExchange)
                .with(reservationCancelledQueue.getName());
    }
}
