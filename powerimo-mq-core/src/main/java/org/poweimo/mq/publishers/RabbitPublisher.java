package org.poweimo.mq.publishers;

import org.poweimo.mq.Message;
import org.poweimo.mq.exceptions.MqException;
import org.poweimo.mq.exceptions.MqListenerException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public interface RabbitPublisher {
    void publish(Message message) throws MqListenerException;
    void publish(String routingKey, Object payload);
}
