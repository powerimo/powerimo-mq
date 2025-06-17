package org.poweimo.mq.handlers;

import org.poweimo.mq.Message;
import org.poweimo.mq.enums.RouteResolution;

/**
 * <p>ExceptionHandler interface.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public interface ExceptionHandler {
    /**
     * <p>handleException.</p>
     *
     * @param message a {@link org.poweimo.mq.Message} object
     * @param ex a {@link java.lang.Exception} object
     * @return a {@link org.poweimo.mq.enums.RouteResolution} object
     */
    RouteResolution handleException(Message message, Exception ex);
}
