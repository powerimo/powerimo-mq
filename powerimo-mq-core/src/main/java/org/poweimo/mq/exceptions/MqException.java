package org.poweimo.mq.exceptions;

/**
 * <p>MqException class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public class MqException extends Exception {

    /**
     * <p>Constructor for MqException.</p>
     */
    public MqException() {
        super();
    }

    /**
     * <p>Constructor for MqException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public MqException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for MqException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param cause a {@link java.lang.Throwable} object
     */
    public MqException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for MqException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object
     */
    public MqException(Throwable cause) {
        super(cause);
    }
}
