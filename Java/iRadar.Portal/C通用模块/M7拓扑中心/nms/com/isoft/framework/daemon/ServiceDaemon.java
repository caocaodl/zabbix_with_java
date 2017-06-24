package com.isoft.framework.daemon;

public interface ServiceDaemon {
	/**
     * This state is used to define when a <code>Fiber</code> has begun the
     * process of pausing its operations. This is the intermedate period where
     * the thread is no longer in the <code>RUNNING</code> status, but not yet
     * to a <code>PAUSED</code> status.
     */
    public static final int PAUSE_PENDING = 5;

    /**
     * This state is used to denote a paused, or otherwise suspended
     * <code>Fiber</code>. When a <code>Fiber</code> is in this state it
     * should not be preforming any work.
     */
    public static final int PAUSED = 6;

    /**
     * This state is used to denote a <code>Fiber</code> recovering from a
     * paused state to a running status. During this status the
     * <code>Fiber</code> is reinitializing any necessary internal elements to
     * re-enter the <code>RUNNING</code> state.
     */
    public static final int RESUME_PENDING = 7;
    
    /**
     * The string names that correspond to the states of the fiber.
     */
    public static final String STATUS_NAMES[] = {
        "START_PENDING", // 0
        "STARTING", // 1
        "RUNNING", // 2
        "STOP_PENDING", // 3
        "STOPPED", // 4
        "PAUSE_PENDING", // 5
        "PAUSED", // 6
        "RESUME_PENDING" // 7
    };

    /**
     * This is the initial <code>Fiber</code> state. When the
     * <code>Fiber</code> begins it startup process it will transition to the
     * <code>STARTING</code> state. A <code>Fiber</code> in a start pending
     * state has not begun any of the initialization process.
     */
    public static final int START_PENDING = 0;

    /**
     * This state is used to define when a <code>Fiber</code> has begun the
     * Initialization process. Once the initialization process is completed the
     * <code>Fiber</code> will transition to a <code>RUNNING</code> status.
     */
    public static final int STARTING = 1;

    /**
     * This state is used to define the normal runtime condition of a
     * <code>Fiber</code>. When a <code>Fiber</code> is in this state then
     * it is processing normally.
     */
    public static final int RUNNING = 2;

    /**
     * This state is used to denote when the <code>Fiber</code> is terminating
     * processing. This state is always followed by the state
     * <code>ST0PPED</code>.
     */
    public static final int STOP_PENDING = 3;

    /**
     * This state represents the final resting state of a <code>Fiber</code>.
     * Depending on the implementation it may be possible to resurrect the
     * <code>Fiber</code> from this state.
     */
    public static final int STOPPED = 4;

    /**
     * This method is used to start the initialization process of the
     * <code>Fiber</code>, which should eventually transition to a
     * <code>RUNNING</code> status.
     */
    void start();

    /**
     * This method is used to stop a currently running <code>Fiber</code>.
     * Once invoked the <code>Fiber</code> should begin it's shutdown process.
     * Depending on the implementation, this method may block until the
     * <code>Fiber</code> terminates.
     */
    void stop();

    /**
     * This method is used to return the name of the <code>Fiber</code>. The
     * name of the instance is defined by the implementor, but it should be
     * realitively unique when possible.
     *
     * @return The name of the <code>Fiber</code>.
     */
    String getName();

    /**
     * This method is used to get the current status of the <code>Fiber</code>.
     * The status of the fiber should be one of the predefined constants of the
     * <code>Fiber</code> interface, or from one of the derived interfaces.
     *
     * @return The current status of the <code>Fiber</code>.
     */
    int getStatus();

    /**
     * This method is used to suspend a currently running <code>Fiber<code>.
     * When invoked the <code>Fiber</code> will begin the transition to
     * a <code>PAUSED</code> status after changing its internal state, if
     * applicable.
     */
    void pause();

    /**
     * This method is used to resume a suspeneded <code>Fiber</code>. If the
     * thread is already running then this method should have no effect on the
     * current <code>Fiber</code>.
     */
    void resume();
    
	 /**
	  * <p>status</p>
	  *
	  * @return a {@link java.lang.String} object.
	  */
	 String getStatusText();
}
