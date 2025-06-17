package org.poweimo.mq.publishers;

import org.poweimo.mq.Message;
import org.poweimo.mq.exceptions.MqException;
import org.poweimo.mq.exceptions.MqListenerException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

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
     * @param message a {@link org.poweimo.mq.Message} object
     * @throws org.poweimo.mq.exceptions.MqListenerException if any.
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
