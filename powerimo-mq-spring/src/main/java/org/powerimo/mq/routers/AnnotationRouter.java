package org.powerimo.mq.routers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.routers.BaseRouter;
import org.powerimo.mq.spring.RabbitAnnotationProcessor;
import org.springframework.context.ApplicationContext;

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
    private final ApplicationContext context;
    private final HashMap<String, Object> messageListeners = new HashMap<>();
    private final HashMap<String, HandlerMethod> messageHandlers = new HashMap<>();

    @Override
    public RouteResolution route(Message message) {
        var routingKey = message.getRoutingKey();
        var handler = findHandlerMethod(routingKey);

        if (handler!=null) {
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
        messageListeners.put(queue, bean);
        log.info("✔️ Registered MQ message listener: {} -> {}", queue, bean.getClass().getCanonicalName());
    }

    public void registerHandler(String routingKey, HandlerMethod method) {
        this.messageHandlers.put(routingKey, method);
        log.info("🪝 Registered MQ message handler: {} -> {}.{}", routingKey,
                method.bean.getClass().getSimpleName(),
                method.method.getName()
        );
    }

    /**
     * <p>Link to bean method</p>
     *
     * @param bean a {@link java.lang.Object} object
     * @param method a {@link java.lang.reflect.Method} object
     */
    public record HandlerMethod(Object bean, Method method) {}
}
