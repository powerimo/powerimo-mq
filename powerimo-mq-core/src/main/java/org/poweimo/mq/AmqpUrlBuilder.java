package org.poweimo.mq;

import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;

/**
 * <p>AmqpUrlBuilder class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public class AmqpUrlBuilder {

    /**
     * <p>buildUrl.</p>
     *
     * @param config a {@link org.poweimo.mq.config.RabbitConfig} object
     * @return a {@link java.lang.String} object
     * @throws org.poweimo.mq.exceptions.InvalidMqConfigurationException if any.
     */
    public static String buildUrl(RabbitConfig config) throws InvalidMqConfigurationException {
        if (config.getUrl() != null)
            return config.getUrl();
        if (config.getHost() == null) {
            throw new InvalidMqConfigurationException("MQ configuration exception: both url and host are empty");
        }
        String result;
        result = "amqp://" + config.getHost() + ":" + config.getPort();
        if (config.getVirtualHost() != null && !config.getVirtualHost().isEmpty()) {
            result = result + "/" + config.getVirtualHost();
        }
        return result;
    }
}
