package org.powerimo.examples.mq.springbootapp;

import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.routers.MessageRouter;
import org.poweimo.mq.routers.RoutingKeyRouter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqRouter {

    @Bean
    public MessageRouter router() {
        return RoutingKeyRouter.builder()
                .handler("key1.event",
                        (message) -> log.info("[MQ->] message received {}", message.toString()))
                .handler("key2.event",
                        (message) -> log.info("[MQ->] key2 message received {}", message.toString()))
                .build();
    }
}
