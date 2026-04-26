package org.powerimo.mq.listeners;

import org.powerimo.mq.enums.ListenerStatus;
import org.powerimo.mq.exceptions.InvalidMqConfigurationException;
import org.powerimo.mq.exceptions.MqListenerException;

/**
 * <p>RabbitListener interface.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public interface RabbitListener {
    /**
     * <p>start.</p>
     *
     * @throws InvalidMqConfigurationException if any.
     * @throws MqListenerException if any.
     */
    void start() throws InvalidMqConfigurationException, MqListenerException;
    /**
     * <p>stop.</p>
     *
     * @throws MqListenerException if any.
     */
    void stop() throws MqListenerException;
    /**
     * <p>getStatus.</p>
     *
     * @return a {@link ListenerStatus} object
     */
    ListenerStatus getStatus();
}
