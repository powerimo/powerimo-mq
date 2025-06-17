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
import org.poweimo.mq.exceptions.MqPublisherException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Default implementation of the RabbitPublisher interface for publishing messages to RabbitMQ.
 * Handles channel initialization, message conversion, and header management using organization-specific configuration and converters.
 * Supports publishing both Message objects and generic payloads with automatic encoding and metadata handling.
 * Throws MqPublisherException on connection, encoding, or publishing errors.
 */
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
        this.rabbitConfig = config;
        this.messageConverter = config.getMessageConverter();

        if (this.messageConverter == null) {
            this.messageConverter = new JsonConverter();
        }
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
     */
    @Override
    public void publish(Message message) {
        initChannelIfNeeded();
        Map<String, Object> headers = new LinkedHashMap<>();
        headers.put(MqConst.DATA_PROTOCOL_HEADER, message.getDataProtocolVersion());
        headers.put(MqConst.DATA_CLASS_HEADER, message.getDataClassName());

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .headers(headers)
                .appId(this.rabbitConfig.getAppId())
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

    protected ConnectionFactory getConnectionFactory() {
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
