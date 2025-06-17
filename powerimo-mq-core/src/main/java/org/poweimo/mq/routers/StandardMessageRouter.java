package org.poweimo.mq.routers;

import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;

public class StandardMessageRouter implements MessageRouter {

    @Override
    public RouteResolution route(Message message) {
        return RouteResolution.DLQ;
    }

}
