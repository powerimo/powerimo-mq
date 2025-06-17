package org.powerimo.examples.mq.springbootapp;

import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.annotations.RabbitMessageHandler;
import org.poweimo.mq.annotations.RabbitMessageListener;
import org.poweimo.mq.routers.MessageRouter;
import org.poweimo.mq.routers.RoutingKeyRouter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RabbitMessageListener(queue = "test")
public class MqRouter {

    @RabbitMessageHandler(routingKey = "key1.event")
    public void handleKey1(Message message) {
        log.info("[MQ->] Key1 message received {}", message.toString());
    }

    @RabbitMessageHandler(routingKeys = {"key2.event", "key5.event"})
    public void handleKey2(Message message) {
        log.info("[MQ->] Key2 message received {}", message.toString());
    }

    @RabbitMessageHandler(routingKey = "key3.event")
    public void handleKey3() {
        throw new RuntimeException("key3.event exception example (planned)");
    }

    @RabbitMessageHandler(routingKey = "key4.event")
    public void handleKey4(SomeData data) {
        log.info("[MQ->] Key4 message received {}", data.toString());
    }
}
