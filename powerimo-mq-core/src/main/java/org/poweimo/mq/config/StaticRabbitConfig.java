package org.poweimo.mq.config;

import lombok.*;

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

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean showConnectionsParameters() {
        return showConnectionParametersFlag;
    }

    @Override
    public String getQueue() {
        return queue;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public long getPort() {
        return port;
    }

    @Override
    public String getVirtualHost() {
        return virtualHost;
    }
}
