package org.poweimo.mq.converters;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.poweimo.mq.Message;

import java.io.IOException;

/**
 * <p>MessageConverter interface.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public interface MessageConverter {

    /**
     * <p>encode.</p>
     *
     * @param payload a {@link java.lang.Object} object
     * @return a {@link org.poweimo.mq.Message} object
     * @throws java.io.IOException if any.
     */
    Message encode(Object payload) throws IOException;

    /**
     * <p>decode.</p>
     *
     * @param consumerTag a {@link java.lang.String} object
     * @param envelope a {@link com.rabbitmq.client.Envelope} object
     * @param basicProperties a {@link com.rabbitmq.client.AMQP.BasicProperties} object
     * @param bytes an array of {@link byte} objects
     * @return a {@link org.poweimo.mq.Message} object
     * @throws java.io.IOException if any.
     * @throws java.lang.ClassNotFoundException if any.
     */
    Message decode(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException, ClassNotFoundException;
}
