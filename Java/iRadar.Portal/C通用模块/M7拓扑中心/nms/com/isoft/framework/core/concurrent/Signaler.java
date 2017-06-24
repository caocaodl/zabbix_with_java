package com.isoft.framework.core.concurrent;

/**
 * <P>
 * The Signaler interface was designed to get around the problem of not being
 * able to extend the functionality of the Object.notify and Object.notifyAll
 * methods. In some instances is would be nice to alter the default behavior
 * slightly, the signaler interface allows this to occur.
 * </P>
 *
 * <P>
 * An object that implements the Signaler interface is used just like a typical
 * object. But instead of using notify and notifyAll, the methods signal and
 * signalAll should be used in their place.
 * </P>
 *
 * @author <A HREF="mailto:weave@oculan.com">Weave </A>
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya </A>
 */
public interface Signaler {
    /**
     * <P>
     * Provides the functionality of the notify method, but may be overridden by
     * the implementor to provide additional functionality.
     * </P>
     *
     * @see java.lang.Object#notify
     */
    public void signal();

    /**
     * <P>
     * Provides the functionality of the notifyAll method, but may be overridden
     * by the implementor to provide additional functionality.
     * </P>
     *
     * @see java.lang.Object#notifyAll
     */
    public void signalAll();
}
