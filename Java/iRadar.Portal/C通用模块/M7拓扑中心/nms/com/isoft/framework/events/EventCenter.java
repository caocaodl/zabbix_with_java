package com.isoft.framework.events;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventCenter {
	private static final Logger LOG = LoggerFactory.getLogger(EventCenter.class);
	
	private static EventIpcManager MGR = new EventIpcManagerDefaultImpl();
	
	public static void registe(EventListener listener, String... ueis) {
		List<String> ueiList =  Arrays.asList(ueis);
		MGR.addEventListener(listener, ueiList);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("registe listener: {} | {}", listener, ueis);
		}
	}
	
	public static void notice(Event e) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("notice event: {}", e);
		}
		MGR.send(e);
	}
	
	
	public static void main(String[] args) {
		registe(new EventListener() {
			@Override
			public void onEvent(Event e) {
				System.out.println(e);
			}
			
			@Override
			public String getName() {
				return "testListener";
			}
		}, "test");
		
		final Event e = new Event();
		e.setUei("test");
		e.setArgs(new EventArgs() {
			@Override
			public String toString() {
				return "testArgs";
			}
		});
		
		new Thread() {

			@Override
			public void run() {
				while (true) {
					notice(e);
				}
			}
		}.run();
	}
}
