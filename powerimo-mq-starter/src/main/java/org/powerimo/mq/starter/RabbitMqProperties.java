package org.powerimo.mq.starter;

import com.rabbitmq.client.Consumer;
import lombok.Getter;
import lombok.Setter;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.converters.MessageConverter;
import org.poweimo.mq.routers.MessageRouter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbit")
@Getter
@Setter
public class RabbitMqProperties implements RabbitConfig {
    private String appId;
    private String url;
    private String host = "localhost";
    private long port = 5672L;
    private String username = "user";
    private String password = "password";
    private String virtualHost;
    private String exchange;
    private String queue;
    private boolean showBanner = true;
    private MessageConverter messageConverter;
    private MessageRouter messageRouter;
    private Consumer consumer;
    private boolean autoStart = true;

    @Override
    public boolean showConnectionsParameters() {
        return showBanner;
    }

}
