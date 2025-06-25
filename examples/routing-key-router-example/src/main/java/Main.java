import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.config.StaticRabbitConfig;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqListenerException;
import org.poweimo.mq.handlers.MessageHandler;
import org.poweimo.mq.listeners.RabbitListenerImpl;
import org.poweimo.mq.publishers.DefaultRabbitPublisher;
import org.poweimo.mq.routers.LogOnlyRouter;
import org.poweimo.mq.routers.RoutingKeyRouter;

@Slf4j
public class Main {
    private static DefaultRabbitPublisher publisher;

    public static void main(String[] args) throws InvalidMqConfigurationException, MqListenerException {
        log.info("Starting application...");

        var router = RoutingKeyRouter.builder()
                .handler("key1.event", (message) -> { log.info("key1 message received: {}", message); })
                .handler("key2.event", (message) -> { log.info("key2 message received: {}", message); })
                .handler("with-exception.event", (message -> { throw new RuntimeException("Some exception"); }))
                .unknownMessageHandler((message) -> {
                    log.info("unknown message received: {}", message);
                    return RouteResolution.ACKNOWLEDGE;
                })
                .exceptionHandler( ((message, ex) -> {
                    log.info("Handler throws exception (planned): {}", ex.getMessage());
                    return RouteResolution.ACKNOWLEDGE;
                }))
                .build();

        var config = StaticRabbitConfig
                .builder()
                .host("localhost")
                .port(5672)
                .username("user")
                .password("password")
                .queue("test")
                .showConnectionParametersFlag(true)
                .exchange("amq.topic")
                .messageRouter(router)
                .build();
        log.info("Configuration: {}", config);

        publisher = initPublisher(config);
        sendSampleMessage("key1.event");
        sendSampleMessage("key2.event");
        sendSampleMessage("key3.event");
        sendSampleMessage("with-exception.event");

        RabbitListenerImpl listener = new RabbitListenerImpl(config);
        listener.start();
    }

    private static DefaultRabbitPublisher initPublisher(StaticRabbitConfig config) {
        return new DefaultRabbitPublisher(config);
    }

    public static void sendSampleMessage(String routingKey) {
        var data = SampleData.builder()
                .attr1("value1")
                .attr2("value2")
                .build();
        publisher.publish(routingKey, data);
        log.info("Published: {}", data);
    }

}
