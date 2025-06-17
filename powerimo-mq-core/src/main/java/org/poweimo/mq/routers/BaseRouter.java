package org.poweimo.mq.routers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.handlers.ExceptionHandler;

/**
 * <p>Abstract BaseRouter class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@Slf4j
public abstract class BaseRouter implements MessageRouter {

    @Getter
    @Setter
    private MessageRouter unknownMessageHandler;

    @Getter
    @Setter
    private ExceptionHandler exceptionHandler;

    /**
     * <p>handleUnknown.</p>
     *
     * @param message a {@link org.poweimo.mq.Message} object
     * @return a {@link org.poweimo.mq.enums.RouteResolution} object
     */
    protected RouteResolution handleUnknown(Message message) {
        if (unknownMessageHandler != null) {
            return unknownMessageHandler.route(message);
        }
        else {
            log.warn("[MQ->] Unsupported routing key: {}. Message will be rejected.", message.getRoutingKey());
            return RouteResolution.DLQ;
        }
    }

    /**
     * <p>handleException.</p>
     *
     * @param message a {@link org.poweimo.mq.Message} object
     * @param ex a {@link java.lang.Exception} object
     * @return a {@link org.poweimo.mq.enums.RouteResolution} object
     */
    protected RouteResolution handleException(Message message, Exception ex) {
        if (exceptionHandler != null) {
            return exceptionHandler.handleException(message, ex);
        } else {
            log.error("Exception on handling message: {}", message, ex);
            return RouteResolution.DLQ;
        }
    }
}
