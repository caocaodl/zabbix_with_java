package com.isoft.framework.events;

/**
 * <p>EventForwarder interface.</p>
 *
 */
public interface EventForwarder {
    
    /**
     * Called by a service to send an event to eventd
     *
     * @param event a {@link org.opennms.netmgt.xml.event.Event} object.
     */
    public void sendNow(Event event);

}
