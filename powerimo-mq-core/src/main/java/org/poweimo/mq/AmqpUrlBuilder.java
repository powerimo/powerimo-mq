package org.poweimo.mq;

import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.exceptions.MqException;

public class AmqpUrlBuilder {

    public static String buildUrl(RabbitConfig config) throws MqException {
        if (config.getUrl() != null)
            return config.getUrl();
        if (config.getHost() == null) {
            throw new MqException("MQ configuration exception: both url and host are empty");
        }
        String result;
        result = "amqp://" + config.getHost() + ":" + config.getPort();
        if (config.getVirtualHost() != null && !config.getVirtualHost().isEmpty()) {
            result = result + "/" + config.getVirtualHost();
        }
        return result;
    }
}
