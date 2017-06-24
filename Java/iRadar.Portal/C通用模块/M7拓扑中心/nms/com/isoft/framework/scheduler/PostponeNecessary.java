package com.isoft.framework.scheduler;

/**
 * Represents a PostponeNecessary
 *
 * @author brozow
 * @version $Id: $
 */
public class PostponeNecessary extends RuntimeException {

    private static final long serialVersionUID = -161577103512338545L;

    /**
     * <p>Constructor for PostponeNecessary.</p>
     */
    public PostponeNecessary() {
        super();
    }

    /**
     * <p>Constructor for PostponeNecessary.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public PostponeNecessary(String message) {
        super(message);
    }

    /**
     * <p>Constructor for PostponeNecessary.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause a {@link java.lang.Throwable} object.
     */
    public PostponeNecessary(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for PostponeNecessary.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public PostponeNecessary(Throwable cause) {
        super(cause);
    }

}
