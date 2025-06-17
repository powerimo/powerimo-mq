package org.poweimo.mq.routers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.handlers.ExceptionHandler;
import org.poweimo.mq.handlers.MessageHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RoutingKeyRouter extends BaseRouter {
    private final Map<String, MessageHandler> routingKeyHandlers = new HashMap<>();

    @Override
    public RouteResolution route(Message message) {
        var routingKey = message.getRoutingKey();
        var handler = routingKeyHandlers.get(routingKey);

        if (handler!=null) {
            try {
                handler.handleMessage(message);
            } catch (Exception ex) {
                return handleException(message, ex);
            }
        } else {
            return handleUnknown(message);
        }
        return RouteResolution.ACKNOWLEDGE;
    }

    public void registerRoutingKeyHandler(String key, MessageHandler handler) {
        routingKeyHandlers.put(key, handler);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MessageRouter unknownMessageHandler;
        private ExceptionHandler exceptionHandler;
        private final HashMap<String, MessageHandler> routingKeyHandlers = new HashMap<>();

        public Builder unknownMessageHandler(MessageRouter handler) {
            this.unknownMessageHandler = handler;
            return this;
        }

        public Builder exceptionHandler(ExceptionHandler handler) {
            this.exceptionHandler = handler;
            return this;
        }

        public Builder handler(String routingKey, MessageHandler handler) {
            this.routingKeyHandlers.put(routingKey, handler);
            return this;
        }

        public RoutingKeyRouter build() {
            var router = new RoutingKeyRouter();
            router.setExceptionHandler(this.exceptionHandler);
            router.setUnknownMessageHandler(this.unknownMessageHandler);

            for(Map.Entry<String, MessageHandler> entry : routingKeyHandlers.entrySet()) {
                router.registerRoutingKeyHandler(entry.getKey(), entry.getValue());
            }

            return router;
        }
    }
}
