package org.powerimo.mq.routers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.annotations.RabbitMessageHandler;
import org.poweimo.mq.annotations.RabbitMessageListener;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.routers.BaseRouter;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class AnnotationRouter extends BaseRouter {
    private final ApplicationContext context;
    private final HashMap<String, Object> messageListeners = new HashMap<>();
    private final HashMap<String, HandlerMethod> messageHandlers = new HashMap<>();

    @PostConstruct
    public void init() {
        Map<String, Object> beans = context.getBeansWithAnnotation(RabbitMessageListener.class);
        for (Object bean : beans.values()) {
            String queue = bean.getClass().getAnnotation(RabbitMessageListener.class).queue();
            messageListeners.put(queue, bean);

            log.info("✔️ Found MQ message listener: {} -> {}", queue, bean.getClass().getCanonicalName());
        }

        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RabbitMessageHandler.class)) {
                    var annotation = method.getAnnotation(RabbitMessageHandler.class);
                    method.setAccessible(true); // for private

                    String routingKey = annotation.routingKey();
                    if (!routingKey.isEmpty()) {
                        messageHandlers.put(routingKey, new HandlerMethod(bean, method));
                    }

                    var routingKeys = annotation.routingKeys();
                    for (String key : routingKeys) {
                        messageHandlers.put(key, new HandlerMethod(bean, method));
                    }

                    log.info("🪝 Found MQ message handler: {} -> {}.{}", routingKey, clazz.getSimpleName(), method.getName());
                }
            }
        }
    }

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

    public HandlerMethod findHandlerMethod(String routingKey) {
        return messageHandlers.get(routingKey);
    }

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

    public record HandlerMethod(Object bean, Method method) {}
}
