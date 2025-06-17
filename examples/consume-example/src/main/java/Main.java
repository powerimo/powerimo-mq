import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqListenerException;
import org.poweimo.mq.listeners.RabbitListenerImpl;
import org.poweimo.mq.config.StaticRabbitConfig;
import org.poweimo.mq.publishers.DefaultRabbitPublisher;
import org.poweimo.mq.routers.LogOnlyRouter;

@Slf4j
public class Main {

    public static void main(String[] args) throws InvalidMqConfigurationException, MqListenerException {
        log.info("Starting application...");

        var config = StaticRabbitConfig
                .builder()
                .host("localhost")
                .port(5672)
                .username("user")
                .password("password")
                .queue("test")
                .showConnectionParametersFlag(true)
                .exchange("amq.topic")
                .messageRouter(new LogOnlyRouter())
                .build();
        log.info("Configuration: {}", config);

        sendSampleMessage(config);

        RabbitListenerImpl listener = new RabbitListenerImpl(config);
        listener.start();
    }

    public static void sendSampleMessage(RabbitConfig config) {
        DefaultRabbitPublisher publisher = new DefaultRabbitPublisher(config);
        var data = SampleData.builder()
                .attr1("value1")
                .attr1("value2")
                .build();
        publisher.publish("sample.event", data);
        log.info("Published: {}", data);
    }

}
