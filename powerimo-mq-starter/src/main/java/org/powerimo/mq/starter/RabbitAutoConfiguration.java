package org.powerimo.mq.starter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Consumer;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.consumers.StandardConsumer;
import org.poweimo.mq.converters.JsonConverter;
import org.poweimo.mq.converters.MessageConverter;
import org.poweimo.mq.exceptions.InvalidMqConfigurationException;
import org.poweimo.mq.exceptions.MqListenerException;
import org.poweimo.mq.listeners.RabbitListener;
import org.poweimo.mq.listeners.RabbitListenerImpl;
import org.poweimo.mq.publishers.DefaultRabbitPublisher;
import org.poweimo.mq.publishers.RabbitPublisher;
import org.poweimo.mq.routers.MessageRouter;
import org.poweimo.mq.routers.RoutingKeyRouter;
import org.powerimo.mq.routers.AnnotationRouter;
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
     * @return a {@link org.poweimo.mq.converters.MessageConverter} object
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new JsonConverter(objectMapper);
    }

    /**
     * <p>rabbitMessageRouter.</p>
     *
     * @param applicationContext a {@link org.springframework.context.ApplicationContext} object
     * @return a {@link org.poweimo.mq.routers.MessageRouter} object
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageRouter rabbitMessageRouter(ApplicationContext applicationContext) {
        return new AnnotationRouter(applicationContext);
    }

    /**
     * <p>rabbitConfig.</p>
     *
     * @param properties a {@link org.powerimo.mq.starter.RabbitMqProperties} object
     * @param messageConverter a {@link org.poweimo.mq.converters.MessageConverter} object
     * @param messageRouter a {@link org.poweimo.mq.routers.MessageRouter} object
     * @param applicationContext a {@link org.springframework.context.ApplicationContext} object
     * @return a {@link org.poweimo.mq.config.RabbitConfig} object
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
     * @param rabbitConfig a {@link org.poweimo.mq.config.RabbitConfig} object
     * @return a {@link org.poweimo.mq.listeners.RabbitListener} object
     */
    @Bean
    @ConditionalOnMissingBean
    public RabbitListener rabbitListener(RabbitConfig rabbitConfig) {
        return new RabbitListenerImpl(rabbitConfig);
    }

    /**
     * <p>rabbitPublisher.</p>
     *
     * @param rabbitConfig a {@link org.poweimo.mq.config.RabbitConfig} object
     * @return a {@link org.poweimo.mq.publishers.RabbitPublisher} object
     */
    @Bean
    @ConditionalOnMissingBean
    public RabbitPublisher rabbitPublisher(RabbitConfig rabbitConfig) {
        return new DefaultRabbitPublisher(rabbitConfig);
    }

    /**
     * <p>listenerStarter.</p>
     *
     * @param rabbitListener a {@link org.poweimo.mq.listeners.RabbitListener} object
     * @param rabbitMqProperties a {@link org.powerimo.mq.starter.RabbitMqProperties} object
     * @return a {@link org.powerimo.mq.starter.ListenerStarter} object
     * @throws org.poweimo.mq.exceptions.InvalidMqConfigurationException if any.
     * @throws org.poweimo.mq.exceptions.MqListenerException if any.
     */
    @Bean
    @ConditionalOnMissingBean
    public ListenerStarter listenerStarter(RabbitListener rabbitListener, RabbitMqProperties rabbitMqProperties) throws InvalidMqConfigurationException, MqListenerException {
        var starter = new ListenerStarter(rabbitListener, rabbitMqProperties);
        starter.checkAutoStart();
        return starter;
    }
}
