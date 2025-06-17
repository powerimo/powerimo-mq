package org.poweimo.mq.routers;

import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;

public interface MessageRouter {
    RouteResolution route(Message message);
}
