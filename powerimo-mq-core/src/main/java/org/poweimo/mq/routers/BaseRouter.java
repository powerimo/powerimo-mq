package org.poweimo.mq.routers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.handlers.ExceptionHandler;

@Slf4j
public abstract class BaseRouter implements MessageRouter {

    @Getter
    @Setter
    private MessageRouter unknownMessageHandler;

    @Getter
    @Setter
    private ExceptionHandler exceptionHandler;

    protected RouteResolution handleUnknown(Message message) {
        if (unknownMessageHandler != null) {
            return unknownMessageHandler.route(message);
        }
        else {
            log.warn("[MQ->] Unsupported routing key: {}. Message will be rejected.", message.getRoutingKey());
            return RouteResolution.DLQ;
        }
    }

    protected RouteResolution handleException(Message message, Exception ex) {
        if (exceptionHandler != null) {
            return exceptionHandler.handleException(message, ex);
        } else {
            log.error("Exception on handling message: {}", message, ex);
            return RouteResolution.DLQ;
        }
    }
}
