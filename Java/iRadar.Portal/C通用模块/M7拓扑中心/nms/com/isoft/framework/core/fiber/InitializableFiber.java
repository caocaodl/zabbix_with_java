package com.isoft.framework.core.fiber;

/**
 * <p>
 * This class is used to extend the <code>Fiber</code> interface so that is
 * has a concept of a life cycle. Prior to starting the fiber the
 * <code>init</code> method will be invoked. Likewise, prior to garbage
 * collection the <code>destroy</code> method should be invoked.
 * </p>
 *
 * @author <a href="mailto:weave@oculan.com">Brian Weaver </a>
 */
public interface InitializableFiber extends Fiber {
    /**
     * This method is used to start the initilization process of the
     * <code>Fiber</code>, which should eventually transition to a
     * <code>RUNNING</code> status.
     */
    public void init();

    /**
     * This method is used to stop a currently running <code>Fiber</code>.
     * Once invoked the <code>Fiber</code> should begin it's shutdown process.
     * Depending on the implementation, this method may block until the
     * <code>Fiber</code> terminates.
     */
    public void destroy();
}
