package org.poweimo.mq.listeners;

import org.poweimo.mq.enums.ListenerStatus;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqListenerException;

public interface RabbitListener {
    void start() throws InvalidMqConfigurationException, MqListenerException;
    void stop() throws MqListenerException;
    ListenerStatus getStatus();
}
