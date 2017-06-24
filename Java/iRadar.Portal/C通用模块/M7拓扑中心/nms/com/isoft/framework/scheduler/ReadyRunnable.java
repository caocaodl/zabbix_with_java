package com.isoft.framework.scheduler;

/**
 * This interface extends the {@link java.lang.Runnable runnable}interface and
 * provides a method to determine if the runnable is ready to start.
 *
 * @author <a href="mailto:weave@oculan.com">Brian Weaver </a>
 * @author <a href="http://www.i-soft.com.cn">i-soft.com.cn </a>
 * @author <a href="mailto:weave@oculan.com">Brian Weaver </a>
 * @author <a href="http://www.i-soft.com.cn">i-soft.com.cn </a>
 * @version $Id: $
 */
public interface ReadyRunnable extends Runnable {
    /**
     * Returns true if the runnable is ready to start.
     *
     * @return a boolean.
     */
    public boolean isReady();
}
