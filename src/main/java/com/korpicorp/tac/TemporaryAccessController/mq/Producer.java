package com.korpicorp.tac.TemporaryAccessController.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Producer {

    private final RabbitTemplate template;
    private final String exchangeName;


    @Autowired
    public Producer(RabbitTemplate template, @Value("${mq.exchangename}") String exchangeNAme) {
        this.template = template;
        this.exchangeName = exchangeNAme;
    }

    public boolean send(byte[] message, Integer ttl) {
        try {
            MessageProperties properties = new MessageProperties();
            properties.setDelay(ttl);
            template.setChannelTransacted(true);
            template.setExchange(exchangeName);
            template.send(exchangeName, "", MessageBuilder.withBody(message).andProperties(properties).build());
            return true;
        } catch (Exception e) {
            log.error("Exception happened: {}", e.getCause().toString());
            return false;
        }
    }
}
