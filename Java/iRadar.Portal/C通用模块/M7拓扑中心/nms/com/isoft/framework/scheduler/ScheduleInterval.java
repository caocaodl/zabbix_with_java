package com.isoft.framework.scheduler;

/**
 * Represents a ScheduleInterval
 *
 * @author brozow
 * @version $Id: $
 */
public interface ScheduleInterval {

    /**
     * <p>getInterval</p>
     *
     * @return a long.
     */
    long getInterval();

    /**
     * <p>scheduledSuspension</p>
     *
     * @return a boolean.
     */
    boolean scheduledSuspension();

}
