package org.powerimo.mq.handlers;

import org.powerimo.mq.Message;

/**
 * <p>MessageHandler interface.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public interface MessageHandler {
    /**
     * <p>handleMessage.</p>
     *
     * @param message a {@link Message} object
     */
    void handleMessage(Message message);
}
