package org.poweimo.mq.config;

import com.rabbitmq.client.Consumer;
import lombok.*;
import org.poweimo.mq.converters.MessageConverter;
import org.poweimo.mq.routers.MessageRouter;

/**
 * Configuration class for RabbitMQ connection parameters, implementing the RabbitConfig interface.
 * Provides static configuration for connection details, queue, exchange, message converter, router, and consumer.
 * Includes builder, getter, setter, and string representation support via Lombok annotations.
 *
 * @author andev
 * @version $Id: $Id
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StaticRabbitConfig implements RabbitConfig {
    private String url;
    private String username;
    private String password;
    private String host;
    private long port;
    private String virtualHost;
    private String exchange;
    private String queue;
    private boolean showConnectionParametersFlag = true;
    private MessageConverter messageConverter;
    private MessageRouter messageRouter;
    private Consumer consumer;
    private String appId;
    private boolean confirmPublish = true;

    /** {@inheritDoc} */
    @Override
    public String getUrl() {
        return url;
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return username;
    }

    /** {@inheritDoc} */
    @Override
    public String getPassword() {
        return password;
    }

    /** {@inheritDoc} */
    @Override
    public boolean showConnectionsParameters() {
        return showConnectionParametersFlag;
    }

    /** {@inheritDoc} */
    @Override
    public String getQueue() {
        return queue;
    }

    /** {@inheritDoc} */
    @Override
    public String getHost() {
        return host;
    }

    /** {@inheritDoc} */
    @Override
    public long getPort() {
        return port;
    }

    /** {@inheritDoc} */
    @Override
    public String getVirtualHost() {
        return virtualHost;
    }

    @Override
    public boolean getConfirmPublish() {
        return confirmPublish;
    }
}
