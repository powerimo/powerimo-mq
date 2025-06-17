package org.poweimo.mq.handlers;

import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;

public interface ExceptionHandler {
    RouteResolution handleException(Message message, Exception ex);
}
