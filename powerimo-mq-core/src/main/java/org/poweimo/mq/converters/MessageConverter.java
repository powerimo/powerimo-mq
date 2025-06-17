package org.poweimo.mq.converters;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.poweimo.mq.Message;

import java.io.IOException;

public interface MessageConverter {
    Message encode(Object payload) throws IOException;
    Message decode(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException, ClassNotFoundException;
}
