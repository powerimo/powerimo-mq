package org.powerimo.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.powerimo.mq.annotations.RabbitMessageListener;
import org.powerimo.mq.routers.MessageRouter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;

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

        return bean;
    }
}
