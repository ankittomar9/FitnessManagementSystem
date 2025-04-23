package com.fitness.activityservice.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    // RabbitMQ configuration properties can be defined here
    // For example, you can define queues, exchanges, and bindings
    // using Spring AMQP annotations or Java configuration.
    // This is a placeholder for RabbitMQ configuration.
    @Bean
    public Queue activityQueue() {
        return new Queue("activity.queue", true);
    }
    @Bean
    public DirectExchange activityExchange() {
        return new DirectExchange("fitness.exchange");
    }

    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange activityExchange) {
        return BindingBuilder.bind(activityQueue).to(activityExchange).with("activity.routing.key");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
      return new Jackson2JsonMessageConverter();
    }
}


/*
* docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4.0-management

* * */