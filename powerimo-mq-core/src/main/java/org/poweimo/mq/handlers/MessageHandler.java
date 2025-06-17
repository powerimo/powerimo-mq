package org.poweimo.mq.handlers;

import org.poweimo.mq.Message;

public interface MessageHandler {
    void handleMessage(Message message);
}
