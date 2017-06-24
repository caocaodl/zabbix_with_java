package com.isoft.framework.events;


public interface EventListener {
    /**
     * Return the id of the listener
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName();

    /**
     * Process a sent event.
     *
     */
    public void onEvent(Event e);
}
