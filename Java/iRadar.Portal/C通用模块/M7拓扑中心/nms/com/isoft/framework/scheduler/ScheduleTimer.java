package com.isoft.framework.scheduler;


/**
 * Represents a ScheduleTimer
 *
 * @author brozow
 * @version $Id: $
 */
public interface ScheduleTimer extends Timer {
    
    /**
     * <p>schedule</p>
     *
     * @param interval a long.
     * @param schedule a {@link com.isoft.framework.scheduler.ReadyRunnable} object.
     */
    public void schedule(long interval, ReadyRunnable schedule);

}
