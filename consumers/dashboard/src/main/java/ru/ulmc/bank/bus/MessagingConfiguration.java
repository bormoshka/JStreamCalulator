package ru.ulmc.bank.bus;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import ru.ulmc.bank.constants.Queues;

/**
 * Конфигурация AMQP
 */
@SpringBootConfiguration
public class MessagingConfiguration {

    @Bean
    Queue queue() {
        return new Queue(Queues.RESPONSE_QUEUE, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("bank-exchange");
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(Queues.RESPONSE_QUEUE);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             @Qualifier("course-request") MessageListener receiver) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(Queues.RESPONSE_QUEUE);
        container.setMessageListener(receiver);
        return container;
    }
}
