package ru.ulmc.bank.server.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.ulmc.bank.constants.Queues;

/**
 * Created by User on 30.04.2017.
 */
@Controller
public class PublishingController {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public PublishingController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String message) {
        rabbitTemplate.convertAndSend(Queues.RESPONSE_QUEUE, message);
    }
}
