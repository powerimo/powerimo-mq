package org.powerimo.mq.starter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.powerimo.mq.config.RabbitConfig;
import org.powerimo.mq.consumers.StandardConsumer;
import org.powerimo.mq.converters.JsonConverter;
import org.powerimo.mq.converters.MessageConverter;
import org.powerimo.mq.exceptions.InvalidMqConfigurationException;
import org.powerimo.mq.exceptions.MqListenerException;
import org.powerimo.mq.listeners.RabbitListener;
import org.powerimo.mq.listeners.RabbitListenerImpl;
import org.powerimo.mq.publishers.DefaultRabbitPublisher;
import org.powerimo.mq.publishers.RabbitPublisher;
import org.powerimo.mq.routers.MessageRouter;
import org.powerimo.mq.routers.AnnotationRouter;
import org.powerimo.mq.spring.RabbitMessageHandlerPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <p>RabbitAutoConfiguration class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@Configuration
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitAutoConfiguration {

    /**
     * <p>objectMapper.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.ObjectMapper} object
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
        mapper.findAndRegisterModules();
        return mapper;
    }

    /**
     * <p>rabbitMessageConverter.</p>
     *
     * @param objectMapper a {@link com.fasterxml.jackson.databind.ObjectMapper} object
     * @return a {@link MessageConverter} object
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new JsonConverter(objectMapper);
    }

    /**
     * <p>rabbitMessageRouter.</p>
     *
     * @return a {@link MessageRouter} object
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageRouter rabbitMessageRouter() {
        return new AnnotationRouter();
    }

    /**
     * <p>rabbitConfig.</p>
     *
     * @param properties a {@link org.powerimo.mq.starter.RabbitMqProperties} object
     * @param messageConverter a {@link MessageConverter} object
     * @param messageRouter a {@link MessageRouter} object
     * @param applicationContext a {@link org.springframework.context.ApplicationContext} object
     * @return a {@link RabbitConfig} object
     */
    @Bean
    @ConditionalOnMissingBean
    @Primary
    public RabbitConfig rabbitConfig(RabbitMqProperties properties, MessageConverter messageConverter, MessageRouter messageRouter,
                                     ApplicationContext applicationContext) {
        if (properties.getMessageConverter() == null) {
            properties.setMessageConverter(messageConverter);
        }
        if (properties.getMessageRouter() == null) {
            properties.setMessageRouter(messageRouter);
        }
        if (properties.getConsumer() == null) {
            properties.setConsumer(new StandardConsumer(properties));
        }
        if (properties.getQueue() == null) {
            properties.setQueue(applicationContext.getId());
        }
        if (properties.getAppId() == null) {
            properties.setAppId(applicationContext.getId());
        }
        return properties;
    }

    /**
     * <p>rabbitListener.</p>
     *
     * @param rabbitConfig a {@link RabbitConfig} object
     * @return a {@link RabbitListener} object
     */
    @Bean
    @ConditionalOnMissingBean
    public RabbitListener rabbitListener(RabbitConfig rabbitConfig) {
        return new RabbitListenerImpl(rabbitConfig);
    }

    /**
     * <p>rabbitPublisher.</p>
     *
     * @param rabbitConfig a {@link RabbitConfig} object
     * @return a {@link RabbitPublisher} object
     */
    @Bean
    @ConditionalOnMissingBean
    public RabbitPublisher rabbitPublisher(RabbitConfig rabbitConfig) {
        return new DefaultRabbitPublisher(rabbitConfig);
    }

    /**
     * <p>listenerStarter.</p>
     *
     * @param rabbitListener a {@link RabbitListener} object
     * @param rabbitMqProperties a {@link org.powerimo.mq.starter.RabbitMqProperties} object
     * @return a {@link org.powerimo.mq.starter.ListenerStarter} object
     * @throws InvalidMqConfigurationException if any.
     * @throws MqListenerException if any.
     */
    @Bean
    @ConditionalOnMissingBean
    public ListenerStarter listenerStarter(RabbitListener rabbitListener, RabbitMqProperties rabbitMqProperties) throws InvalidMqConfigurationException, MqListenerException {
        var starter = new ListenerStarter(rabbitListener, rabbitMqProperties);
        starter.checkAutoStart();
        return starter;
    }

    /**
     * <p>PostProcessor scans beans for Listener annotation and register these into router</p>
     *
     * @param messageRouter Rabbit message router
     * @return a PostProcessor
     */
    @Bean
    public static RabbitMessageHandlerPostProcessor rabbitMessageHandlerPostProcessor(MessageRouter messageRouter) {
        return new RabbitMessageHandlerPostProcessor(messageRouter);
    }
}
