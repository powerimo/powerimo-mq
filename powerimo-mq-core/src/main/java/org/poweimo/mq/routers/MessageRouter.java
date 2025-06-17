package org.poweimo.mq.routers;

import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;

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
     * @param message a {@link org.poweimo.mq.Message} object
     * @return a {@link org.poweimo.mq.enums.RouteResolution} object
     */
    RouteResolution route(Message message);
}
