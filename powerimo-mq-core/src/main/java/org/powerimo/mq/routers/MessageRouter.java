package org.powerimo.mq.routers;

import org.powerimo.mq.Message;
import org.powerimo.mq.enums.RouteResolution;

/**
 * <p>MessageRouter interface.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public interface MessageRouter {
    /**
     * <p>route.</p>
     *
     * @param message a {@link Message} object
     * @return a {@link RouteResolution} object
     */
    RouteResolution route(Message message);
}
