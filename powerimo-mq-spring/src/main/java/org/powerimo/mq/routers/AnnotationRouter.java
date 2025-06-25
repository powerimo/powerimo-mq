package org.powerimo.mq.routers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.annotations.RabbitMessageHandler;
import org.poweimo.mq.annotations.RabbitDefaultHandler;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.routers.BaseRouter;
import org.powerimo.mq.spring.RabbitAnnotationProcessor;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * <p>AnnotationRouter class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@RequiredArgsConstructor
@Slf4j
public class AnnotationRouter extends BaseRouter implements RabbitAnnotationProcessor {

    @Getter
    private final HashMap<String, ListenerDescriptor> messageListeners = new HashMap<>();
    private final HashMap<String, HandlerMethod> messageHandlers = new HashMap<>();

    @Override
    public RouteResolution route(Message message) {
        var routingKey = message.getRoutingKey();
        var handler = findHandlerMethod(routingKey);

        if (handler != null) {
            try {
                handle(handler, message);
            } catch (Exception ex) {
                return handleException(message, ex);
            }
        } else {
            return handleUnknown(message);
        }
        return RouteResolution.ACKNOWLEDGE;
    }

    /**
     * <p>findHandlerMethod.</p>
     *
     * @param routingKey a {@link java.lang.String} object
     * @return a {@link org.powerimo.mq.routers.AnnotationRouter.HandlerMethod} object
     */
    public HandlerMethod findHandlerMethod(String routingKey) {
        return messageHandlers.get(routingKey);
    }

    /**
     * <p>handle.</p>
     *
     * @param handler a {@link org.powerimo.mq.routers.AnnotationRouter.HandlerMethod} object
     * @param message a {@link org.poweimo.mq.Message} object
     */
    public void handle(HandlerMethod handler, Message message) {
        Method method = handler.method;
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            if (Message.class.isAssignableFrom(paramType)) {
                args[i] = message;
            } else if (message.getPayloadClass() != null && paramType.isAssignableFrom(message.getPayloadClass())) {
                args[i] = message.getPayload();
            }
        }

        try {
            method.invoke(handler.bean, args);
        } catch (Exception ex) {
            throw new RuntimeException("Error handling " + method.getName(), ex);
        }
    }

    public void registerListener(String queue, Object bean) {
        ListenerDescriptor descriptor = new ListenerDescriptor(bean, null);
        messageListeners.put(queue, descriptor);
        log.info("✔️ Registered MQ message listener: {} -> {}", queue, bean.getClass().getCanonicalName());
        processListener(descriptor);
    }

    public void registerHandler(String routingKey, HandlerMethod method) {
        this.messageHandlers.put(routingKey, method);
        log.info("🪝 Registered MQ message handler: {} -> {}.{}", routingKey,
                method.bean.getClass().getSimpleName(),
                method.method.getName()
        );
    }

    protected void processListener(ListenerDescriptor descriptor) {
        Class<?> clazz = AopUtils.getTargetClass(descriptor.bean);

        for (Method method : clazz.getDeclaredMethods()) {

            // RabbitMessageHandler annotation
            if (method.isAnnotationPresent(RabbitMessageHandler.class)) {
                RabbitMessageHandler handler = method.getAnnotation(RabbitMessageHandler.class);
                method.setAccessible(true);

                if (handler.routingKey() != null && !handler.routingKey().isEmpty()) {
                    registerHandler(handler.routingKey(), new AnnotationRouter.HandlerMethod(descriptor.bean, method));
                }
                if (handler.routingKeys() != null) {
                    for (String routingKey : handler.routingKeys()) {
                        registerHandler(routingKey, new AnnotationRouter.HandlerMethod(descriptor.bean, method));
                    }
                }
            }

            // RabbitUnknownHandler
            if (method.isAnnotationPresent(RabbitDefaultHandler.class)) {
                if (descriptor.defaultHandlerMethod == null) {
                    method.setAccessible(true);
                    descriptor.defaultHandlerMethod = new HandlerMethod(descriptor.bean, method);
                    log.info("🪝 Default message handler found: {}.{}",
                            descriptor.bean.getClass().getSimpleName(),
                            method.getName());
                }
            }

        }
    }

    /**
     * <p>Find listener by queue name</p>
     *
     * @param queue queue name
     * @return descriptor
     */
    public ListenerDescriptor findListenerDescriptor(String queue) {
        return messageListeners.get(queue);
    }

    /**
     * <p>Find listener descriptor by bean</p>
     *
     * @param bean Spring bean
     * @return descriptor
     */
    public ListenerDescriptor findListenerDescriptor(Object bean) {
        for (var entry : messageListeners.entrySet()) {
            if (entry.getValue().bean == bean) {
                return findListenerDescriptor(entry.getKey());
            }
        }
        return null;
    }

    /**
     * <p>Link to bean method</p>
     *
     * @param bean   a {@link java.lang.Object} object
     * @param method a {@link java.lang.reflect.Method} object
     */
    public record HandlerMethod(Object bean, Method method) {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ListenerDescriptor {
        private Object bean;
        private HandlerMethod defaultHandlerMethod;
    }


    @Override
    protected RouteResolution handleUnknown(Message message) {
        for (var entry : messageListeners.entrySet()) {
            var descriptor = entry.getValue();
            if (descriptor.defaultHandlerMethod != null) {
                handle(descriptor.defaultHandlerMethod, message);
                return RouteResolution.ACKNOWLEDGE;
            }
        }
        return RouteResolution.DLQ;
    }

}
