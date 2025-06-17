package org.poweimo.mq.exceptions;

/**
 * <p>InvalidMqConfigurationException class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
public class InvalidMqConfigurationException extends Exception {

    /**
     * <p>Constructor for InvalidMqConfigurationException.</p>
     */
    public InvalidMqConfigurationException() {
        super();
    }

    /**
     * <p>Constructor for InvalidMqConfigurationException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public InvalidMqConfigurationException(String message) {
        super(message);
    }

    /**
     * <p>Constructor for InvalidMqConfigurationException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param cause a {@link java.lang.Throwable} object
     */
    public InvalidMqConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for InvalidMqConfigurationException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object
     */
    public InvalidMqConfigurationException(Throwable cause) {
        super(cause);
    }
}
