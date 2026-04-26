package org.powerimo.mq.routers;

import lombok.extern.slf4j.Slf4j;
import org.powerimo.mq.Message;
import org.powerimo.mq.enums.RouteResolution;

/**
 * <p>LogOnlyRouter class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@Slf4j
public class LogOnlyRouter implements MessageRouter {

    /** {@inheritDoc} */
    @Override
    public RouteResolution route(Message message) {
        log.info("[MQ->] {}", message);
        return RouteResolution.ACKNOWLEDGE;
    }
}
