package org.poweimo.mq.listeners;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.AmqpUrlBuilder;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.config.StaticRabbitConfig;
import org.poweimo.mq.consumers.ChannelSupport;
import org.poweimo.mq.consumers.StandardConsumer;
import org.poweimo.mq.converters.JsonConverter;
import org.poweimo.mq.converters.MessageConverter;
import org.poweimo.mq.enums.ListenerStatus;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqListenerException;
import org.poweimo.mq.routers.AllToDlqMessageRouter;
import org.poweimo.mq.routers.MessageRouter;
import org.powerimo.common.utils.Utils;

/**
 * Implementation of RabbitListener for managing RabbitMQ message consumption.
 * Handles connection setup, channel creation, consumer initialization, and listener lifecycle (start/stop).
 * Supports configurable RabbitConfig, Consumer, MessageRouter, and MessageConverter.
 * Provides status tracking and logs connection details when enabled.
 *
 * @author andev
 * @version $Id: $Id
 */
@Slf4j
public class RabbitListenerImpl implements RabbitListener {
    private ListenerStatus status;

    @Getter
    @Setter
    private RabbitConfig rabbitConfiguration;

    @Setter
    private ConnectionFactory connectionFactory;

    @Getter
    private Channel channel;

    @Setter
    private Consumer consumer;

    /**
     * <p>Constructor for RabbitListenerImpl.</p>
     *
     * @param rabbitConfiguration a {@link org.poweimo.mq.config.RabbitConfig} object
     */
    public RabbitListenerImpl(RabbitConfig rabbitConfiguration) {
        this.rabbitConfiguration = rabbitConfiguration;
    }

    /**
     * <p>Constructor for RabbitListenerImpl.</p>
     *
     * @param rabbitConfiguration a {@link org.poweimo.mq.config.RabbitConfig} object
     * @param connectionFactory a {@link com.rabbitmq.client.ConnectionFactory} object
     */
    public RabbitListenerImpl(RabbitConfig rabbitConfiguration, ConnectionFactory connectionFactory) {
        this.rabbitConfiguration = rabbitConfiguration;
    }

    /**
     * {@inheritDoc}
     *
     * Starts the RabbitListener by establishing a connection to RabbitMQ, creating a channel,
     * and beginning message consumption on the configured queue. If already running, does nothing.
     */
    @Override
    public void start() throws InvalidMqConfigurationException, MqListenerException {
        if (status == ListenerStatus.RUNNING) {
            log.debug("Listener is already started");
            return;
        }
        if (rabbitConfiguration.getQueue() == null) {
            throw new InvalidMqConfigurationException("Queue is not specified");
        }

        logConnectionInfo();
        try {
            getConnectionFactory().setUri(AmqpUrlBuilder.buildUrl(rabbitConfiguration));

            // username and password could be specified in URL, therefore
            if (rabbitConfiguration.getUsername() != null) {
                connectionFactory.setUsername(rabbitConfiguration.getUsername());
                connectionFactory.setPassword(rabbitConfiguration.getPassword());
            }
            Connection mqConnection = connectionFactory.newConnection();
            channel = mqConnection.createChannel();
            channel.basicConsume(rabbitConfiguration.getQueue(), getConsumer());
            status = ListenerStatus.RUNNING;
            log.info("RabbitListener started on listening queue: {}", this.rabbitConfiguration.getQueue());
        } catch (InvalidMqConfigurationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MqListenerException("RabbitListener is not started", ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Stops the RabbitListener by closing the underlying channel and updating the listener status to STOPPED.
     * If the listener is already stopped, the method returns immediately.
     */
    @Override
    public void stop() throws MqListenerException {
        if (status == ListenerStatus.STOPPED) {
            return;
        }

        try {
            channel.close();
            status = ListenerStatus.STOPPED;
        } catch (Exception ex) {
            throw new MqListenerException("Unable to stop Listener", ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Returns the current status of the listener.
     */
    @Override
    public ListenerStatus getStatus() {
        return status;
    }

    /**
     * Returns the current Consumer instance, initializing a default StandardConsumer with a StandardMessageRouter
     * and JsonConverter if none is set. Ensures the Consumer has the correct Channel assigned.
     *
     * @return the Consumer used for message consumption
     */
    public Consumer getConsumer() {
        if (consumer != null) {
            return consumer;
        }

        if (rabbitConfiguration.getConsumer() != null) {
            consumer = rabbitConfiguration.getConsumer();
        }

        if (consumer == null) {
            MessageRouter router = rabbitConfiguration.getMessageRouter();
            if (router == null) {
                router = new AllToDlqMessageRouter();
            }

            MessageConverter messageConverter = rabbitConfiguration.getMessageConverter();
            if (messageConverter == null) {
                messageConverter = new JsonConverter();
            }

            var consumerConfig = StaticRabbitConfig.builder()
                    .messageRouter(router)
                    .messageConverter(messageConverter)
                    .build();

            log.warn("Consumer is missing. Default consumer will be used.");
            StandardConsumer standardConsumer = new StandardConsumer(consumerConfig);
            standardConsumer.setChannel(channel);
            consumer = standardConsumer;
        }

        // set channel to Consumer
        if (consumer instanceof ChannelSupport channelSupport) {
            channelSupport.setChannel(channel);
        }

        return consumer;
    }

    /**
     * <p>logConnectionInfo.</p>
     */
    protected void logConnectionInfo() {
        if (!rabbitConfiguration.showConnectionsParameters())
            return;
        if (rabbitConfiguration.getUrl() != null) {
            var msg = Utils.formatLogValue("RabbitMQ URL", rabbitConfiguration.getUrl());
            log.info(msg);
            msg = Utils.formatLogValue("RabbitMQ Username", rabbitConfiguration.getUsername());
            log.info(msg);
        } else {
            var msg = Utils.formatLogValue("RabbitMQ Host", rabbitConfiguration.getHost());
            log.info(msg);
            msg = Utils.formatLogValue("RabbitMQ Port", rabbitConfiguration.getPort());
            log.info(msg);
            msg = Utils.formatLogValue("RabbitMQ VirtualHost", rabbitConfiguration.getVirtualHost());
            log.info(msg);
        }

        log.info(Utils.formatLogValue("RabbitMQ queue", rabbitConfiguration.getQueue()));
        log.info(Utils.formatLogValue("RabbitMQ client App ID", rabbitConfiguration.getAppId()));
        log.info(Utils.formatLogValue("RabbitMQ exchange", rabbitConfiguration.getExchange()));
    }

    /**
     * <p>Getter for the field <code>connectionFactory</code>.</p>
     *
     * @return a {@link com.rabbitmq.client.ConnectionFactory} object
     */
    protected ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new ConnectionFactory();
        }
        return connectionFactory;
    }


}
