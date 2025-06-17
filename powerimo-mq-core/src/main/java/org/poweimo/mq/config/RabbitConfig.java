package org.poweimo.mq.config;

public interface RabbitConfig {
    String getUrl();
    String getUsername();
    String getPassword();
    boolean showConnectionsParameters();
    String getQueue();
    String getHost();
    long getPort();
    String getVirtualHost();
    String getExchange();
}
