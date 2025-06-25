package org.poweimo.mq.config;

import com.rabbitmq.client.Consumer;
import org.poweimo.mq.converters.MessageConverter;
import org.poweimo.mq.routers.MessageRouter;

/**
 * <p>RabbitConfig interface.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public interface RabbitConfig {
    /**
     * <p>getAppId.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getAppId();
    /**
     * <p>getUrl.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getUrl();
    /**
     * <p>getUsername.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getUsername();
    /**
     * <p>getPassword.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getPassword();

    /**
     * <p>True if confirm is needed otherwise False</p>
     *
      * @return a boolean
     */
    boolean getConfirmPublish();
    /**
     * <p>showConnectionsParameters.</p>
     *
     * @return a boolean
     */
    boolean showConnectionsParameters();
    /**
     * <p>getQueue.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getQueue();
    /**
     * <p>getHost.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getHost();
    /**
     * <p>getPort.</p>
     *
     * @return a long
     */
    long getPort();
    /**
     * <p>getVirtualHost.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getVirtualHost();
    /**
     * <p>getExchange.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getExchange();
    /**
     * <p>getMessageConverter.</p>
     *
     * @return a {@link org.poweimo.mq.converters.MessageConverter} object
     */
    MessageConverter getMessageConverter();
    /**
     * <p>getMessageRouter.</p>
     *
     * @return a {@link org.poweimo.mq.routers.MessageRouter} object
     */
    MessageRouter getMessageRouter();
    /**
     * <p>getConsumer.</p>
     *
     * @return a {@link com.rabbitmq.client.Consumer} object
     */
    Consumer getConsumer();
}
