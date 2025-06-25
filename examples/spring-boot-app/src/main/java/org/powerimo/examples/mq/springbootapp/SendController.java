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

    @GetMapping("all")
    public ResponseEntity<?> sendAll() {
        rabbitPublisher.publish("key1.event", null);
        rabbitPublisher.publish("key2.event", null);
        rabbitPublisher.publish("key3.event", null);

        var data = new SomeData();
        data.setStringAttr("test-string");
        data.setIntAttr(123);
        rabbitPublisher.publish("key4.event", data);
        rabbitPublisher.publish("key5.event", data);
        rabbitPublisher.publish("key6.event", data);
        rabbitPublisher.publish("key7.event", data);
        return ResponseEntity.ok().body("Sent");
    }

}
