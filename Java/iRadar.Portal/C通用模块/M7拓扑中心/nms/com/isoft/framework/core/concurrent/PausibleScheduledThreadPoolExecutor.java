package com.isoft.framework.core.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PausibleScheduledThreadPoolExecutor
 *
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @version $Id: $
 */
public class PausibleScheduledThreadPoolExecutor extends
        ScheduledThreadPoolExecutor {
    
    private AtomicBoolean isPaused = new AtomicBoolean(false);
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unpaused = pauseLock.newCondition();

    public PausibleScheduledThreadPoolExecutor(final int corePoolSize) {
        super(corePoolSize);
    }
    
    public PausibleScheduledThreadPoolExecutor(final int corePoolSize, final ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }
    
    /**
     * <p>isPaused</p>
     *
     * @return a boolean.
     */
    public boolean isPaused() {
        return isPaused.get();
    }

    /** {@inheritDoc} */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
      super.beforeExecute(t, r);
      pauseLock.lock();
      try {
        while (isPaused.get()) unpaused.await();
      } catch(InterruptedException ie) {
        t.interrupt();
      } finally {
        pauseLock.unlock();
      }
    }
  
    /**
     * <p>pause</p>
     */
    public void pause() {
      pauseLock.lock();
      try {
        isPaused.set(true);
      } finally {
        pauseLock.unlock();
      }
    }
  
    /**
     * <p>resume</p>
     */
    public void resume() {
      pauseLock.lock();
      try {
        isPaused.set(false);
        unpaused.signalAll();
      } finally {
        pauseLock.unlock();
      }
    }




}
