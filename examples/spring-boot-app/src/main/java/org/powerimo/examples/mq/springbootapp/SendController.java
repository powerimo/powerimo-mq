package org.powerimo.examples.mq.springbootapp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.publishers.RabbitPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send")
@RequiredArgsConstructor
@Slf4j
public class SendController {
    private final RabbitPublisher rabbitPublisher;

    @GetMapping
    public ResponseEntity<?> sendMessage(
            @RequestParam(name = "message") String message,
            @RequestParam(name = "routing_key", required = false) String routingKey) {
        var rk = routingKey != null ? routingKey : "key1.event";
        rabbitPublisher.publish(rk, message);
        log.info("Sending message to RabbitMQ. RoutingKey: {}; message: {}", rk, message);
        return ResponseEntity.ok().body("Sent");
    }

}
