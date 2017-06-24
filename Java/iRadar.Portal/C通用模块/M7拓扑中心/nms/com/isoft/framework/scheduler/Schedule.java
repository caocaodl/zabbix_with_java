package com.isoft.framework.scheduler;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Schedule
 * 
 * @author brozow
 * @version $Id: $
 */
public class Schedule {

	private static final Logger LOG = LoggerFactory.getLogger(Schedule.class);

	/** Constant <code>random</code> */
	private static final Random random = new Random();

	private final ReadyRunnable m_schedulable;
	private final ScheduleInterval m_interval;
	private final ScheduleTimer m_timer;
	private volatile int m_currentExpirationCode;
	private volatile boolean m_scheduled = false;

	class ScheduleEntry implements ReadyRunnable {
		private final int m_expirationCode;

		public ScheduleEntry(int expirationCode) {
			m_expirationCode = expirationCode;
		}

		/**
		 * @return
		 */
		private boolean isExpired() {
			return m_expirationCode < m_currentExpirationCode;
		}

		@Override
		public boolean isReady() {
			return isExpired() || m_schedulable.isReady();
		}

		@Override
		public void run() {
			if (isExpired()) {
				LOG.debug("Schedule {} expired.  No need to run.", this);
				return;
			}

			if (!m_interval.scheduledSuspension()) {
				try {
					Schedule.this.run();
				} catch (PostponeNecessary e) {
					// Chose a random number of seconds between 5 and 14 to wait
					// before trying again
					m_timer.schedule(random.nextInt(10) * 1000 + 5000, this);
					return;
				}
			}

			// if it is expired by the current run then don't reschedule
			if (isExpired()) {
				LOG.debug("Schedule {} expired.  No need to reschedule.", this);
				return;
			}

			long interval = m_interval.getInterval();
			if (interval >= 0 && m_scheduled)
				m_timer.schedule(interval, this);

		}

		@Override
		public String toString() {
			return "ScheduleEntry[expCode=" + m_expirationCode + "] for " + m_schedulable;
		}
	}

	/**
	 * <p>
	 * Constructor for Schedule.
	 * </p>
	 * 
	 * @param interval
	 *            a {@link com.isoft.framework.scheduler.ScheduleInterval}
	 *            object.
	 * @param timer
	 *            a {@link com.isoft.framework.scheduler.ScheduleTimer} object.
	 * @param schedulable
	 *            a {@link com.isoft.framework.scheduler.ReadyRunnable} object.
	 */
	public Schedule(ReadyRunnable schedulable, ScheduleInterval interval, ScheduleTimer timer) {
		m_schedulable = schedulable;
		m_interval = interval;
		m_timer = timer;
		m_currentExpirationCode = 0;
	}

	/**
	 * <p>
	 * schedule
	 * </p>
	 */
	public void schedule() {
		m_scheduled = true;
		schedule(0);
	}

	private void schedule(long interval) {
		if (interval >= 0 && m_scheduled)
			m_timer.schedule(interval, new ScheduleEntry(++m_currentExpirationCode));
	}

	/**
	 * <p>
	 * run
	 * </p>
	 */
	public void run() {
		m_schedulable.run();
	}

	/**
	 * <p>
	 * adjustSchedule
	 * </p>
	 */
	public void adjustSchedule() {
		schedule(m_interval.getInterval());
	}

	/**
	 * <p>
	 * unschedule
	 * </p>
	 */
	public void unschedule() {
		m_scheduled = false;
		m_currentExpirationCode++;
	}

}
