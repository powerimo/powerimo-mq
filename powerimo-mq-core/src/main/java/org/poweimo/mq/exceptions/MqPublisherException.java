package org.poweimo.mq.exceptions;

/**
 * <p>MqPublisherException class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public class MqPublisherException extends RuntimeException {

    /**
     * <p>Constructor for MqPublisherException.</p>
     */
    public MqPublisherException() {
        super();
    }

    /**
     * <p>Constructor for MqPublisherException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public MqPublisherException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for MqPublisherException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param cause a {@link java.lang.Throwable} object
     */
    public MqPublisherException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for MqPublisherException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object
     */
    public MqPublisherException(Throwable cause) {
        super(cause);
    }
}
