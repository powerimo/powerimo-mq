package org.poweimo.mq;

import com.rabbitmq.client.Envelope;
import lombok.*;

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

    public Class<?> getPayloadClass() {
        if (payload == null)
            return null;
        return payload.getClass();
    }
}
