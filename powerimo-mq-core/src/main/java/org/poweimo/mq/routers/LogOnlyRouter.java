package org.poweimo.mq.routers;

import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;

@Slf4j
public class LogOnlyRouter implements MessageRouter {

    @Override
    public RouteResolution route(Message message) {
        log.info("[MQ->] {}", message);
        return RouteResolution.ACKNOWLEDGE;
    }
}
