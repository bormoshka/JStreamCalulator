package ru.ulmc.generator.logic;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.ulmc.bank.constants.Queues;
import ru.ulmc.bank.core.serialization.CommonJsonSerializer;
import ru.ulmc.generator.logic.beans.QuoteEntity;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by User on 30.04.2017.
 */
@Controller
public class PublishingController {
    private final RabbitTemplate rabbitTemplate;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final CommonJsonSerializer<QuoteEntity> jsonSerializer = new CommonJsonSerializer<>(QuoteEntity.class);

    @Autowired
    public PublishingController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PreDestroy
    public void close() {
        executor.shutdown();
    }

    public void publish(QuoteEntity message) {
        message.setDatetime(LocalDateTime.now());
        executor.execute(() -> rabbitTemplate.convertAndSend(Queues.BASE_QUOTES_QUEUE, jsonSerializer.serialize(message)));
    }
}
