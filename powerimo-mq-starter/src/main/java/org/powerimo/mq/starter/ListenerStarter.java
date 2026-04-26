package org.powerimo.mq.starter;

import lombok.Getter;
import lombok.Setter;
import org.powerimo.mq.exceptions.InvalidMqConfigurationException;
import org.powerimo.mq.exceptions.MqListenerException;
import org.powerimo.mq.listeners.RabbitListener;

/**
 * <p>ListenerStarter class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@Getter
@Setter
public class ListenerStarter {
    private final RabbitListener listener;
    private final RabbitMqProperties properties;

    /**
     * <p>Constructor for ListenerStarter.</p>
     *
     * @param listener a {@link RabbitListener} object
     * @param properties a {@link org.powerimo.mq.starter.RabbitMqProperties} object
     */
    public ListenerStarter(RabbitListener listener, RabbitMqProperties properties) {
        this.listener = listener;
        this.properties = properties;
    }

    /**
     * <p>checkAutoStart.</p>
     *
     * @throws InvalidMqConfigurationException if any.
     * @throws MqListenerException if any.
     */
    public void checkAutoStart() throws InvalidMqConfigurationException, MqListenerException {
        if (properties.isAutoStart()) {
            listener.start();
        }
    }
}
