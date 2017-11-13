package ru.ulmc.bank.bus;

import com.google.common.collect.EvictingQueue;
import lombok.Getter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Queue;

/**
 * Created by User on 30.04.2017.
 */
@Component
@Getter
public class SurrogateMessageStorage {
    private final Queue<Message> fifo = EvictingQueue.create(20);

    @Bean
    @Qualifier("course-request")
    public MessageListener messageReceiver() {
        return message -> {
            fifo.add(message);
        };
    }
}
