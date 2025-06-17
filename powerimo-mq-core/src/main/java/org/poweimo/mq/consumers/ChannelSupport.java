package org.poweimo.mq.consumers;

import com.rabbitmq.client.Channel;

/**
 * <p>ChannelSupport interface.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public interface ChannelSupport {
    /**
     * <p>setChannel.</p>
     *
     * @param channel a {@link com.rabbitmq.client.Channel} object
     */
    void setChannel(Channel channel);
}
