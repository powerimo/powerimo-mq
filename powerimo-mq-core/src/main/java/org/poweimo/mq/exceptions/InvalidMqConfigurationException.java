package org.poweimo.mq.exceptions;

public class InvalidMqConfigurationException extends Exception {

    public InvalidMqConfigurationException() {
        super();
    }

    public InvalidMqConfigurationException(String message) {
        super(message);
    }

    public InvalidMqConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMqConfigurationException(Throwable cause) {
        super(cause);
    }
}
