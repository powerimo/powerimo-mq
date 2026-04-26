package org.powerimo.mq.publishers;

import org.powerimo.mq.Message;
import org.powerimo.mq.exceptions.MqListenerException;

/**
 * <p>RabbitPublisher interface.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public interface RabbitPublisher {
    /**
     * <p>publish.</p>
     *
     * @param message a {@link Message} object
     * @throws MqListenerException if any.
     */
    void publish(Message message) throws MqListenerException;
    /**
     * <p>publish.</p>
     *
     * @param routingKey a {@link java.lang.String} object
     * @param payload a {@link java.lang.Object} object
     */
    void publish(String routingKey, Object payload);
}
