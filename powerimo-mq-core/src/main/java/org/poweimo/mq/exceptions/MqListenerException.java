package org.poweimo.mq.exceptions;

/**
 * <p>MqListenerException class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public class MqListenerException extends Exception {

    /**
     * <p>Constructor for MqListenerException.</p>
     */
    public MqListenerException() {
        super();
    }

    /**
     * <p>Constructor for MqListenerException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public MqListenerException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for MqListenerException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param cause a {@link java.lang.Throwable} object
     */
    public MqListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for MqListenerException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object
     */
    public MqListenerException(Throwable cause) {
        super(cause);
    }
}
