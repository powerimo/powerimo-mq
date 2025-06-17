package org.poweimo.mq.listeners;

import com.rabbitmq.client.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.AmqpUrlBuilder;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.consumers.ChannelSupport;
import org.poweimo.mq.consumers.StandardConsumer;
import org.poweimo.mq.converters.JsonConverter;
import org.poweimo.mq.enums.ListenerStatus;
import org.poweimo.mq.exceptions.MqException;
import org.poweimo.mq.routers.StandardMessageRouter;
import org.powerimo.common.utils.Utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitListenerImpl implements RabbitListener {
    private ListenerStatus status;

    @Getter
    @Setter
    private RabbitConfig parameters;

    @Setter
    private ConnectionFactory connectionFactory;

    @Getter
    private Channel channel;

    @Setter
    private Consumer consumer;

    public RabbitListenerImpl(RabbitConfig parameters) {
        this.parameters = parameters;
    }

    public RabbitListenerImpl(RabbitConfig parameters, ConnectionFactory connectionFactory) {
        this.parameters = parameters;
    }

    @Override
    public void start() throws MqException {
        if (status == ListenerStatus.RUNNING) {
            log.debug("Listener is already started");
            return;
        }
        if (parameters.getQueue() == null) {
            throw new MqException("Queue is not specified");
        }

        logConnectionInfo();
        try {
            getConnectionFactory().setUri(AmqpUrlBuilder.buildUrl(parameters));

            // username and password could be specified in URL, therefore
            if (parameters.getUsername() != null) {
                connectionFactory.setUsername(parameters.getUsername());
                connectionFactory.setPassword(parameters.getPassword());
            }
            Connection mqConnection = connectionFactory.newConnection();
            channel = mqConnection.createChannel();
            channel.basicConsume(parameters.getQueue(), getConsumer());
            status = ListenerStatus.RUNNING;
            log.info("RabbitListener started on listening queue: {}", this.parameters.getQueue());
        } catch (MqException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MqException("RabbitListener is not started", ex);
        }
    }

    @Override
    public void stop() throws IOException, TimeoutException {
        if (status == ListenerStatus.STOPPED) {
            return;
        }
        channel.close();
        status = ListenerStatus.STOPPED;
    }

    @Override
    public ListenerStatus getStatus() {
        return null;
    }

    public Consumer getConsumer() {
        if (consumer == null) {
            log.warn("Consumer is missing. Default consumer will be used.");
            var router = new StandardMessageRouter();
            var converter = new JsonConverter();
            StandardConsumer standardConsumer = new StandardConsumer(router, converter);
            standardConsumer.setChannel(channel);
            consumer = standardConsumer;
        }

        if (consumer instanceof ChannelSupport channelSupport) {
            channelSupport.setChannel(channel);
        }

        return consumer;
    }

    private void logConnectionInfo() {
        if (!parameters.showConnectionsParameters())
            return;
        if (parameters.getUrl() != null) {
            var msg = Utils.formatLogValue("RabbitMQ URL", parameters.getUrl());
            log.info(msg);
            msg = Utils.formatLogValue("RabbitMQ Username", parameters.getUsername());
            log.info(msg);
        } else {
            var msg = Utils.formatLogValue("RabbitMQ Host", parameters.getHost());
            log.info(msg);
            msg = Utils.formatLogValue("RabbitMQ Port", parameters.getPort());
            log.info(msg);
            msg = Utils.formatLogValue("RabbitMQ VirtualHost", parameters.getVirtualHost());
            log.info(msg);
        }

        log.info(Utils.formatLogValue("RabbitMQ queue", parameters.getQueue()));
    }

    private ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new ConnectionFactory();
        }
        return connectionFactory;
    }


}
