package org.powerimo.mq.starter;

import lombok.Getter;
import lombok.Setter;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqListenerException;
import org.poweimo.mq.listeners.RabbitListener;

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
     * @param listener a {@link org.poweimo.mq.listeners.RabbitListener} object
     * @param properties a {@link org.powerimo.mq.starter.RabbitMqProperties} object
     */
    public ListenerStarter(RabbitListener listener, RabbitMqProperties properties) {
        this.listener = listener;
        this.properties = properties;
    }

    /**
     * <p>checkAutoStart.</p>
     *
     * @throws org.poweimo.mq.exceptions.InvalidMqConfigurationException if any.
     * @throws org.poweimo.mq.exceptions.MqListenerException if any.
     */
    public void checkAutoStart() throws InvalidMqConfigurationException, MqListenerException {
        if (properties.isAutoStart()) {
            listener.start();
        }
    }
}
