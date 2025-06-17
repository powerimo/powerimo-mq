package org.poweimo.mq.consumers;

import com.rabbitmq.client.Channel;

public interface ChannelSupport {
    void setChannel(Channel channel);
}
