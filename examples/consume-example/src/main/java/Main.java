import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.listeners.RabbitListenerImpl;
import org.poweimo.mq.config.StaticRabbitConfig;
import org.poweimo.mq.consumers.StandardConsumer;
import org.poweimo.mq.converters.JsonConverter;
import org.poweimo.mq.exceptions.MqException;
import org.poweimo.mq.publishers.DefaultRabbitPublisher;
import org.poweimo.mq.routers.LogOnlyRouter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Main {

    public static void main(String[] args) throws MqException, IOException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
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
                .build();

        sendSampleMessage(config);

        StandardConsumer consumer = new StandardConsumer(new LogOnlyRouter(), new JsonConverter());

        log.info("Config: {}", config);

        RabbitListenerImpl listener = new RabbitListenerImpl(config);
        listener.setConsumer(consumer);
        listener.start();
    }

    public static void sendSampleMessage(RabbitConfig config) throws IOException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException, TimeoutException, MqException {
        DefaultRabbitPublisher publisher = new DefaultRabbitPublisher(config);
        var data = SampleData.builder()
                .attr1("value1")
                .attr1("value2")
                .build();
        publisher.publish("sample.event", data);
        log.info("Published: {}", data);
    }

}
