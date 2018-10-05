package ru.ulmc.bank.core.bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

    @Override
    public void onMessage(Message message) {
        LOGGER.debug("Got message in main application: {}", message);
    }
}
