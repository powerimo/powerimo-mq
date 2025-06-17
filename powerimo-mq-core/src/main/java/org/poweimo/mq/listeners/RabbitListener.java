package org.poweimo.mq.listeners;

import org.poweimo.mq.enums.ListenerStatus;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqListenerException;

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
     * @throws org.poweimo.mq.exceptions.InvalidMqConfigurationException if any.
     * @throws org.poweimo.mq.exceptions.MqListenerException if any.
     */
    void start() throws InvalidMqConfigurationException, MqListenerException;
    /**
     * <p>stop.</p>
     *
     * @throws org.poweimo.mq.exceptions.MqListenerException if any.
     */
    void stop() throws MqListenerException;
    /**
     * <p>getStatus.</p>
     *
     * @return a {@link org.poweimo.mq.enums.ListenerStatus} object
     */
    ListenerStatus getStatus();
}
