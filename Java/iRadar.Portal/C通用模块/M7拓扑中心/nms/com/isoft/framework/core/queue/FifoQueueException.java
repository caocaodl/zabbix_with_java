package com.isoft.framework.core.queue;

/**
 * <p>
 * The root of all exceptions dealing with queues that implement the
 * {@link FifoQueue FifoQueue} interface. This exception is the general purpose
 * exception that is thrown when a queue error occurs.
 * </p>
 *
 * @author <a href="mailto:weave@oculan.com">Brian Weaver </a>
 */
public class FifoQueueException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 4596596920225763462L;

    /**
     * Constructs a default instance of the exception with no message.
     */
    public FifoQueueException() {
        super();
    }

    /**
     * Constructs a new instance of the exception with the specific message.
     *
     * @param why
     *            The message associated with the exception
     */
    public FifoQueueException(String why) {
        super(why);
    }
}
