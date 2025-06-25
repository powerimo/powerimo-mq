package org.poweimo.mq.converters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.poweimo.mq.Message;
import org.poweimo.mq.MqConst;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class JsonConverterTest {

    @Test
    @DisplayName("Successfully encodes a non-null payload object into a Message with correct protocol version, class metadata, and JSON body.")
    void testEncodeWithValidPayload() throws IOException {
        JsonConverter converter = new JsonConverter();
        TestPayload payload = new TestPayload("foo", 42);

        Message message = converter.encode(payload);

        assertEquals(MqConst.DATA_PROTOCOL_VERSION_1_3, message.getDataProtocolVersion());
        assertEquals(TestPayload.class.getCanonicalName(), message.getDataClassName());
        assertNotNull(message.getBody());
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        assertTrue(json.contains("\"name\":\"foo\""));
        assertTrue(json.contains("\"value\":42"));
    }

    @Test
    @DisplayName("Successfully decodes a Message with a valid data-class header, reconstructing the original payload object.")
    void testDecodeWithDataClassHeader() throws Exception {
        JsonConverter converter = new JsonConverter();
        TestPayload payload = new TestPayload("bar", 99);
        String json = new ObjectMapper().writeValueAsString(payload);
        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        Map<String, Object> headers = new HashMap<>();
        headers.put(MqConst.DATA_CLASS_HEADER, TestPayload.class.getCanonicalName());
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().headers(headers).build();
        Envelope envelope = new Envelope(1L, false, "exchange", "routingKey");

        Message message = converter.decode("tag", envelope, props, body);

        assertEquals("routingKey", message.getRoutingKey());
        assertEquals(TestPayload.class.getCanonicalName(), message.getDataClassName());
        assertNotNull(message.getPayload());
        assertInstanceOf(TestPayload.class, message.getPayload());
        TestPayload decoded = (TestPayload) message.getPayload();
        assertEquals("bar", decoded.getName());
        assertEquals(99, decoded.getValue());
    }

    @Test
    @DisplayName("Successfully decodes a Message without a data-class header, deserializing the payload as a generic Map.")
    void testDecodeWithoutDataClassHeader() throws Exception {
        JsonConverter converter = new JsonConverter();
        Map<String, Object> original = new HashMap<>();
        original.put("foo", "bar");
        original.put("num", 123);
        String json = new ObjectMapper().writeValueAsString(original);
        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        Map<String, Object> headers = new HashMap<>();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().headers(headers).build();
        Envelope envelope = new Envelope(2L, false, "exchange", "rk");

        Message message = converter.decode("tag", envelope, props, body);

        assertEquals("rk", message.getRoutingKey());
        assertNull(message.getDataClassName());
        assertNotNull(message.getPayload());
        assertInstanceOf(Map.class, message.getPayload());
        Map<?, ?> decoded = (Map<?, ?>) message.getPayload();
        assertEquals("bar", decoded.get("foo"));
        assertEquals(123, decoded.get("num"));
    }

    @Test
    @DisplayName("Handles encoding when the payload is null, returning a Message with only the protocol version set and no body.")
    void testEncodeWithNullPayload() throws IOException {
        JsonConverter converter = new JsonConverter();

        Message message = converter.encode(null);

        assertEquals(MqConst.DATA_PROTOCOL_VERSION_1_3, message.getDataProtocolVersion());
        assertNull(message.getBody());
        assertNull(message.getDataClassName());
        assertNull(message.getPayload());
    }

    @Test
    @DisplayName("Handles decoding when the message body is null or empty, resulting in a Message with a null payload.")
    void testDecodeWithNullOrEmptyBody() throws Exception {
        JsonConverter converter = new JsonConverter();

        Map<String, Object> headers = new HashMap<>();
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().headers(headers).build();
        Envelope envelope = new Envelope(3L, false, "exchange", "rk2");

        // Test with null body
        Message messageNull = converter.decode("tag", envelope, props, null);
        assertNull(messageNull.getPayload());

        // Test with empty body
        Message messageEmpty = converter.decode("tag", envelope, props, new byte[0]);
        assertNull(messageEmpty.getPayload());
    }

    @Test
    @DisplayName("Throws an exception when the data-class header is of an unsupported type during extraction.")
    void testExtractDataClassNameWithUnsupportedHeaderType() {
        JsonConverter converter = new JsonConverter();
        Map<String, Object> headers = new HashMap<>();
        headers.put(MqConst.DATA_CLASS_HEADER, 12345); // Integer is unsupported
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().headers(headers).build();
        Message message = Message.builder().amqpBasicProperties(props).build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            converter.extractDataClassName(message);
        });
        assertTrue(ex.getMessage().contains("Unsupported header type"));
    }

}