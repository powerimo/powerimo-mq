package org.powerimo.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.annotations.RabbitMessageHandler;
import org.poweimo.mq.annotations.RabbitMessageListener;
import org.poweimo.mq.routers.MessageRouter;
import org.powerimo.mq.routers.AnnotationRouter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
public class RabbitMessageHandlerPostProcessor implements BeanPostProcessor {
    private final MessageRouter router;
    private final RabbitAnnotationProcessor annotationProcessor;

    public RabbitMessageHandlerPostProcessor(MessageRouter router) {
        this.router = router;
        if (router instanceof RabbitAnnotationProcessor) {
            annotationProcessor = (RabbitAnnotationProcessor) router;
        }  else {
            annotationProcessor = null;
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (annotationProcessor == null) {
            log.debug("MQ message router {} doesn't support annotation registration. Skipped.", router.getClass().getSimpleName());
            return bean;
        }

        Class<?> clazz = AopUtils.getTargetClass(bean);

        if (clazz.isAnnotationPresent(RabbitMessageListener.class)) {
            RabbitMessageListener listener = clazz.getAnnotation(RabbitMessageListener.class);
            if (listener != null && listener.queue() != null) {
                annotationProcessor.registerListener(listener.queue(), bean);
            }
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RabbitMessageHandler.class)) {
                RabbitMessageHandler handler = method.getAnnotation(RabbitMessageHandler.class);
                method.setAccessible(true);

                if (handler.routingKey() != null) {
                    annotationProcessor.registerHandler(handler.routingKey(), new AnnotationRouter.HandlerMethod(bean, method));
                }
                if (handler.routingKeys() != null) {
                    for (String routingKey : handler.routingKeys()) {
                        annotationProcessor.registerHandler(routingKey, new AnnotationRouter.HandlerMethod(bean, method));
                    }
                }
            }
        }

        return bean;
    }
}
