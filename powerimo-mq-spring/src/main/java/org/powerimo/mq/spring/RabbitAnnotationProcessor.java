package org.powerimo.mq.spring;

import org.powerimo.mq.routers.AnnotationRouter;

public interface RabbitAnnotationProcessor {
    void registerListener(String queue, Object bean);
    void registerHandler(String routingKey, AnnotationRouter.HandlerMethod method);

}
