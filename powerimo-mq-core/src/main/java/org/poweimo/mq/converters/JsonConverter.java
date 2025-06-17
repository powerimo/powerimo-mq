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

@Slf4j
@Getter
public class JsonConverter implements MessageConverter {
    private final ObjectMapper mapper;

    public JsonConverter() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        log.warn("ObjectMapper is missing. Default ObjectMapper was created.");
    }

    public JsonConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

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

    @Override
    public Message decode(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes)
            throws IOException, ClassNotFoundException {

        var message = Message.builder()
                .body(bytes)
                .routingKey(envelope.getRoutingKey())
                .envelope(envelope)
                .build();

        var bodyAsString = new String(bytes, StandardCharsets.UTF_8);

        Object headerClassName = basicProperties.getHeaders() != null
                ? basicProperties.getHeaders().get(MqConst.DATA_CLASS_HEADER)
                : null;

        if (headerClassName != null) {
            String className;
            if (headerClassName instanceof String) {
                className = (String) headerClassName;
            } else if (headerClassName instanceof byte[]) {
                className = new String((byte[]) headerClassName, StandardCharsets.UTF_8);
            } else if (headerClassName instanceof LongString) {
                className = headerClassName.toString();
            } else {
                throw new IllegalArgumentException("Unsupported header type for dataClassName: " + headerClassName.getClass());
            }

            Class<?> cls = Class.forName(className);
            message.setDataClassName(cls.getCanonicalName());

            Object payload;
            if (cls == String.class) {
                // Не десериализуем, если указан String
                payload = bodyAsString;
            } else {
                payload = mapper.readValue(bodyAsString, cls);
            }

            message.setPayload(payload);
        } else {
            // Нет заголовка — читаем как Map<String, Object>
            message.setPayload(mapper.readValue(bodyAsString, new TypeReference<Map<String, Object>>() {}));
        }

        return message;
    }


}
