package org.poweimo.mq.handlers;

import org.poweimo.mq.Message;

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
     * @param message a {@link org.poweimo.mq.Message} object
     */
    void handleMessage(Message message);
}
