package com.isoft.framework.events;


/**
 * This is the interface used to send events into the event subsystem - It is
 * typically used by the poller framework plugins that perform service
 * monitoring to send out appropriate events. Can also be used by capsd,
 * discovery etc.
 *
 */
public interface EventProxy {
    /**
     * This method is called to send the event out
     *
     * @param event
     *            the event to be sent out
     * @exception EventProxyException
     *                thrown if the send fails for any reason
     */
    public void send(Event event);

}
