package com.isoft.framework.scheduler;

/**
 * <p>Scheduler interface.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public interface Scheduler extends ScheduleTimer {

	/**
	 * {@inheritDoc}
	 *
	 * This method is used to schedule a ready runnable in the system. The
	 * interval is used as the key for determining which queue to add the
	 * runnable.
	 */
	public abstract void schedule(long interval, final ReadyRunnable runnable);

	/**
	 * This returns the current time for the scheduler
	 *
	 * @return a long.
	 */
	public abstract long getCurrentTime();

	/**
	 * Starts the fiber.
	 *
	 * @throws java.lang.IllegalStateException
	 *             Thrown if the fiber is already running.
	 */
	public abstract void start();

	/**
	 * Stops the fiber. If the fiber has never been run then an exception is
	 * generated.
	 *
	 * @throws java.lang.IllegalStateException
	 *             Throws if the fiber has never been started.
	 */
	public abstract void stop();

	/**
	 * Pauses the scheduler if it is current running. If the fiber has not been
	 * run or has already stopped then an exception is generated.
	 *
	 * @throws java.lang.IllegalStateException
	 *             Throws if the operation could not be completed due to the
	 *             fiber's state.
	 */
	public abstract void pause();

	/**
	 * Resumes the scheduler if it has been paused. If the fiber has not been
	 * run or has already stopped then an exception is generated.
	 *
	 * @throws java.lang.IllegalStateException
	 *             Throws if the operation could not be completed due to the
	 *             fiber's state.
	 */
	public abstract void resume();

	/**
	 * Returns the current of this fiber.
	 *
	 * @return The current status.
	 */
	public abstract int getStatus();

	/**
     * Returns the total number of scheduled tasks (ReadyRunnables) that have
     * been executed since the scheduler was initialized.
     *
     * @return the number of task executed
     */
    public abstract long getNumTasksExecuted();
}
