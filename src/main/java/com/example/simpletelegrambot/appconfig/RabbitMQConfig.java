package com.example.simpletelegrambot.appconfig;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    String queueName = "telegram_bot_queue";

    String exchange = "telegram_bot_exchange";

    private String routingKey = "telegram_bot_routing_key";

    String queueName2 = "my_offer_queue";

    String exchange2 = "my_offer_exchange";

    private String routingKey2 = "my_offer_routing_key";

    String queueName3 = "accepted_offer_queue";

    String exchange3 = "accepted_offer_exchange";

    private String routingKey3 = "accepted_offer_routing_key";

    String queueName4 = "stop_queue";

    String exchange4 = "stop_exchange";

    private String routingKey4 = "stop_routing_key";

    @Bean
    Queue queue1() {
        return new Queue(queueName, true);
    }

    @Bean
    DirectExchange exchange1() {
        return new DirectExchange(exchange);
    }

    @Bean
    Binding binding1(Queue queue1, DirectExchange exchange1) {
        return BindingBuilder.bind(queue1).to(exchange1).with(routingKey);
    }

    @Bean
    Queue queue2() {
        return new Queue(queueName2, true);
    }

    @Bean
    DirectExchange exchange2() {
        return new DirectExchange(exchange2);
    }

    @Bean
    Binding binding2(Queue queue2, DirectExchange exchange2) {
        return BindingBuilder.bind(queue2).to(exchange2).with(routingKey2);
    }
    @Bean
    Queue queue3() {
        return new Queue(queueName3, true);
    }

    @Bean
    DirectExchange exchange3() {
        return new DirectExchange(exchange3);
    }

    @Bean
    Binding binding3(Queue queue3, DirectExchange exchange3) {
        return BindingBuilder.bind(queue3).to(exchange3).with(routingKey3);
    }
    @Bean
    Queue queue4() {
        return new Queue(queueName4, true);
    }

    @Bean
    DirectExchange exchange4() {
        return new DirectExchange(exchange4);
    }

    @Bean
    Binding binding4(Queue queue4, DirectExchange exchange4) {
        return BindingBuilder.bind(queue4).to(exchange4).with(routingKey4);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
