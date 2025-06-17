package org.poweimo.mq.exceptions;

public class MqPublisherException extends RuntimeException {

    public MqPublisherException() {
        super();
    }

    public MqPublisherException(String message) {
        super(message);
    }

    public MqPublisherException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqPublisherException(Throwable cause) {
        super(cause);
    }
}
