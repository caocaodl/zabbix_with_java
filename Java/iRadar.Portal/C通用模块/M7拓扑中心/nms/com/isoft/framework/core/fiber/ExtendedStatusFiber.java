package com.isoft.framework.core.fiber;

/**
 * <p>
 * This interface defines an extension to the {@link Fiber Fiber}class for
 * getting extended status information. The definition of extended status is
 * defined by the implementation. The concept is to provide additional status
 * information to knowledgable code, while not breaking the normal status
 * information defined by the <code>Fiber</code> interface.
 * </p>
 *
 * @author <a href="mailto:weave@oculan.com">Brian Weaver </a>
 */
public interface ExtendedStatusFiber extends Fiber {
    /**
     * Returns the extended status information for the implementation. The
     * definiton of extended status is defined by the implementation.
     *
     * @return The extended status information.
     */
    public int getExtendedStatus();
}
