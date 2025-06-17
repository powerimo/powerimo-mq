package org.poweimo.mq.listeners;

import org.poweimo.mq.enums.ListenerStatus;
import org.poweimo.mq.exceptions.MqException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface RabbitListener {
    void start() throws MqException;
    void stop() throws IOException, TimeoutException;
    ListenerStatus getStatus();
}
