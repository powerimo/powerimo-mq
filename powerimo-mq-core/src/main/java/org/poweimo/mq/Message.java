package org.poweimo.mq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.impl.AMQBasicProperties;
import lombok.*;

/**
 * <p>Message class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String routingKey;
    private byte[] body;
    private String dataProtocolVersion;
    private String dataClassName;
    private Object payload;
    private Envelope envelope;
    private AMQP.BasicProperties amqpBasicProperties;

    /**
     * <p>getPayloadClass.</p>
     *
     * @return a {@link java.lang.Class} object
     */
    public Class<?> getPayloadClass() {
        if (payload == null)
            return null;
        return payload.getClass();
    }

}
