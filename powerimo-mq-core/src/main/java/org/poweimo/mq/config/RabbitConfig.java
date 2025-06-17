package org.poweimo.mq.config;

import com.rabbitmq.client.Consumer;
import org.poweimo.mq.converters.MessageConverter;
import org.poweimo.mq.routers.MessageRouter;

public interface RabbitConfig {
    String getAppId();
    String getUrl();
    String getUsername();
    String getPassword();
    boolean showConnectionsParameters();
    String getQueue();
    String getHost();
    long getPort();
    String getVirtualHost();
    String getExchange();
    MessageConverter getMessageConverter();
    MessageRouter getMessageRouter();
    Consumer getConsumer();
}
