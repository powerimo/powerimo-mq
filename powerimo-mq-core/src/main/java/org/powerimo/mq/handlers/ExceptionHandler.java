package org.powerimo.mq.handlers;

import org.powerimo.mq.Message;
import org.powerimo.mq.enums.RouteResolution;

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
     * @param message a {@link Message} object
     * @param ex a {@link java.lang.Exception} object
     * @return a {@link RouteResolution} object
     */
    RouteResolution handleException(Message message, Exception ex);
}
