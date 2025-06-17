package org.poweimo.mq.publishers;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.AmqpUrlBuilder;
import org.poweimo.mq.Message;
import org.poweimo.mq.MqConst;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.converters.JsonConverter;
import org.poweimo.mq.converters.MessageConverter;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqException;
import org.poweimo.mq.exceptions.MqListenerException;
import org.poweimo.mq.exceptions.MqPublisherException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
public class DefaultRabbitPublisher implements RabbitPublisher {
    private ConnectionFactory connectionFactory;
    private final RabbitConfig rabbitConfig;
    private Channel channel;
    private MessageConverter messageConverter;

    public DefaultRabbitPublisher(RabbitConfig config, ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        this.connectionFactory = connectionFactory;
        this.rabbitConfig = config;
    }

    public DefaultRabbitPublisher(RabbitConfig config) {
        this.messageConverter = new JsonConverter();
        this.rabbitConfig = config;
    }

    protected void initChannelIfNeeded() {
        if (channel != null) {
            return;
        }

        try {
            var url = AmqpUrlBuilder.buildUrl(rabbitConfig);
            getConnectionFactory().setUri(url);
            var cn = getConnectionFactory().newConnection();
            channel = cn.createChannel();
        } catch (IOException | TimeoutException | URISyntaxException | NoSuchAlgorithmException |
                 KeyManagementException | InvalidMqConfigurationException e) {
            throw new MqPublisherException("Exception on creating channel", e);
        }
    }

    /**
     * Publishes the given Message to the configured RabbitMQ exchange using the specified routing key and message headers.
     * Initializes the channel if needed, sets protocol and class headers, and logs the sent message.
     *
     * @param message the Message object to be published
     * @throws IOException              if an I/O error occurs
     * @throws URISyntaxException       if the RabbitMQ URI is invalid
     * @throws NoSuchAlgorithmException if a security algorithm is not available
     * @throws KeyManagementException   if there is a key management error
     * @throws TimeoutException         if a timeout occurs during connection
     * @throws MqException              if a custom MQ error occurs
     */
    @Override
    public void publish(Message message) {
        initChannelIfNeeded();
        Map<String, Object> headers = new LinkedHashMap<>();
        headers.put(MqConst.DATA_PROTOCOL_HEADER, message.getDataProtocolVersion());
        headers.put(MqConst.DATA_CLASS_HEADER, message.getDataClassName());

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .headers(headers)
                .build();

        try {
            channel.basicPublish(
                    rabbitConfig.getExchange(),
                    message.getRoutingKey(),
                    props,
                    message.getBody()
            );
        } catch (IOException ex) {
            throw new MqPublisherException("Error publishing message", ex);
        }
        log.debug("[->MQ] message sent: {}", message);
    }

    @Override
    public void publish(String routingKey, Object payload) {
        Message message;
        try {
            message = messageConverter.encode(payload);
        } catch (IOException ex) {
            throw new MqPublisherException("Error encoding payload", ex);
        }

        message.setRoutingKey(routingKey);
        message.setDataProtocolVersion(MqConst.DATA_PROTOCOL_VERSION_1_3);

        if (payload != null) {
            message.setDataClassName(payload.getClass().getCanonicalName());
        }

        try {
            publish(message);
        } catch (Exception ex) {
            throw new MqPublisherException("Error publishing message", ex);
        }
    }

    private ConnectionFactory getConnectionFactory() {
        if (connectionFactory != null) {
            return connectionFactory;
        }
        connectionFactory = new ConnectionFactory();
        if (rabbitConfig.getUsername() != null) {
            connectionFactory.setUsername(rabbitConfig.getUsername());
            connectionFactory.setPassword(rabbitConfig.getPassword());
        }
        return connectionFactory;
    }
}
