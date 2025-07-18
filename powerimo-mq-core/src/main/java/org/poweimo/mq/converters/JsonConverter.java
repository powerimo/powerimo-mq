package org.poweimo.mq.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.LongString;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.MqConst;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * MessageConverter implementation for serializing and deserializing messages using JSON.
 * Utilizes an ObjectMapper for JSON processing, supporting protocol versioning and class metadata.
 * Handles unknown properties gracefully and supports both typed and generic payloads.
 *
 * @author andev
 * @version $Id: $Id
 */
@Slf4j
@Getter
public class JsonConverter implements MessageConverter {
    private final ObjectMapper mapper;
    private final static String SPRING_DATA_CLASS_HEADER = "__TypeId__";

    /**
     * Default constructor that initializes the ObjectMapper with registered modules
     * and configures it to ignore unknown, ignored, and null creator properties during deserialization.
     * Logs a warning if a default ObjectMapper is created.
     */
    public JsonConverter() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        log.warn("ObjectMapper is missing. Default ObjectMapper was created.");
    }

    /**
     * <p>Constructor for JsonConverter.</p>
     *
     * @param mapper a {@link com.fasterxml.jackson.databind.ObjectMapper} object
     */
    public JsonConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     *
     * Serializes the given payload object to JSON and constructs a Message with protocol version and class metadata.
     * If the payload is null, returns a Message with only the protocol version set.
     */
    @Override
    public Message encode(Object payload) throws IOException {
        if (payload == null) {
            return Message.builder()
                    .dataProtocolVersion(MqConst.DATA_PROTOCOL_VERSION_1_3)
                    .build();
        } else {
            var data = this.mapper.writeValueAsString(payload);
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            return Message.builder()
                    .dataProtocolVersion(MqConst.DATA_PROTOCOL_VERSION_1_3)
                    .dataClassName(payload.getClass().getCanonicalName())
                    .body(bytes)
                    .build();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Deserializes the given byte array into a Message object using metadata from the provided envelope and AMQP properties.
     * Determines the payload type from the data-class header if present, otherwise deserializes as Map.
     */
    @Override
    public Message decode(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes)
            throws IOException, ClassNotFoundException {

        var message = Message.builder()
                .body(bytes)
                .routingKey(envelope.getRoutingKey())
                .envelope(envelope)
                .amqpBasicProperties(basicProperties)
                .build();

        String bodyAsString = null;
        if (bytes != null) {
            bodyAsString = new String(bytes, StandardCharsets.UTF_8);
        }

        var className = this.extractDataClassName(message);

        if (className != null) {
            Class<?> cls = Class.forName(className);
            message.setDataClassName(cls.getCanonicalName());

            Object payload;
            if (cls == String.class) {
                // don't deserialize if String
                payload = bodyAsString;
            } else {
                payload = mapper.readValue(bodyAsString, cls);
            }

            message.setPayload(payload);
        } else if (bodyAsString == null || bodyAsString.isEmpty()) {
            message.setPayload(null);
        } else {
            // there is no data-class header. Read as Map<String, Object>
            message.setPayload(mapper.readValue(bodyAsString, new TypeReference<Map<String, Object>>() {
            }));
        }

        return message;
    }

    public String extractDataClassName(Message message) {
        Object headerClassName = message.getAmqpBasicProperties().getHeaders() != null
                ? message.getAmqpBasicProperties().getHeaders().get(MqConst.DATA_CLASS_HEADER)
                : null;

        // Spring support
        if (headerClassName == null) {
            headerClassName = message.getAmqpBasicProperties().getHeaders().get(SPRING_DATA_CLASS_HEADER);
        }

        if (headerClassName != null) {
            String className;

            // class name could be represented in different formats
            if (headerClassName instanceof String) {
                className = (String) headerClassName;
            } else if (headerClassName instanceof byte[]) {
                className = new String((byte[]) headerClassName, StandardCharsets.UTF_8);
            } else if (headerClassName instanceof LongString) {
                className = headerClassName.toString();
            } else {
                throw new IllegalArgumentException("Unsupported header type for dataClassName: " + headerClassName.getClass());
            }
            return className;
        }
        return null;
    }

}
