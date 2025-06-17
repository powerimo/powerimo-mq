package org.poweimo.mq.exceptions;

public class MqListenerException extends Exception {

    public MqListenerException() {
        super();
    }

    public MqListenerException(String message) {
        super(message);
    }

    public MqListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqListenerException(Throwable cause) {
        super(cause);
    }
}
