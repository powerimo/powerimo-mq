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

/**
 * <p>RoutingKeyRouter class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@Slf4j
public class RoutingKeyRouter extends BaseRouter {
    private final Map<String, MessageHandler> routingKeyHandlers = new HashMap<>();

    /** {@inheritDoc} */
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

    /**
     * <p>registerRoutingKeyHandler.</p>
     *
     * @param key a {@link java.lang.String} object
     * @param handler a {@link org.poweimo.mq.handlers.MessageHandler} object
     */
    public void registerRoutingKeyHandler(String key, MessageHandler handler) {
        routingKeyHandlers.put(key, handler);
    }

    /**
     * <p>builder.</p>
     *
     * @return a {@link org.poweimo.mq.routers.RoutingKeyRouter.Builder} object
     */
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
