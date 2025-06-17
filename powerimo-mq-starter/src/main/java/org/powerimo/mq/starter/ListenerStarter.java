package org.powerimo.mq.starter;

import lombok.Getter;
import lombok.Setter;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqListenerException;
import org.poweimo.mq.listeners.RabbitListener;

@Getter
@Setter
public class ListenerStarter {
    private final RabbitListener listener;
    private final RabbitMqProperties properties;

    public ListenerStarter(RabbitListener listener, RabbitMqProperties properties) {
        this.listener = listener;
        this.properties = properties;
    }

    public void checkAutoStart() throws InvalidMqConfigurationException, MqListenerException {
        if (properties.isAutoStart()) {
            listener.start();
        }
    }
}
