package ru.ulmc.generator;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import ru.ulmc.bank.constants.Queues;
import ru.ulmc.bank.core.serialization.CommonJsonSerializer;

/**
 * Created by User on 30.04.2017.
 */
@Controller
public class PublishingController {
    private final RabbitTemplate rabbitTemplate;
    private final CommonJsonSerializer<QuoteEntity> jsonSerializer = new CommonJsonSerializer(QuoteEntity.class);

    @Autowired
    public PublishingController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(QuoteEntity message) {
        rabbitTemplate.convertAndSend(Queues.BASE_QUOTES_QUEUE, jsonSerializer.serialize(message));
    }
}
