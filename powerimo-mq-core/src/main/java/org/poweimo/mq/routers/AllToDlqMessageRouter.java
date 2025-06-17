package org.poweimo.mq.routers;

import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;

/**
 * MessageRouter implementation that routes all messages to the DLQ.
 * Logs a warning each time a message is routed.
 *
 * @see MessageRouter
 * @see RouteResolution#DLQ
 * @author andev
 * @version $Id: $Id
 */
@Slf4j
public class AllToDlqMessageRouter implements MessageRouter {

    /** {@inheritDoc} */
    @Override
    public RouteResolution route(Message message) {
        log.warn("[MQ->] message routed to DLQ {}", message.toString());
        return RouteResolution.DLQ;
    }

}
