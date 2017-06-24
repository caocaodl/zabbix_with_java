package com.isoft.framework.events;

import com.isoft.imon.topo.util.DateUtil;

public class Event {

	private String _uei;
	private String _time;
	private EventArgs _args;

	public Event() {
		_time = DateUtil.getCurrentDateTime();
	}
	public Event(String uei) {
		this();
		setUei(uei);
	}
	

	public EventArgs getArgs() {
		return _args;
	}

	public void setArgs(EventArgs args) {
		this._args = args;
	}

	public void setUei(final String uei) {
		_uei = uei;
	}

	public String getUei() {
		return _uei;
	}

	public String getTime() {
		return _time;
	}

	@Override
	public String toString() {
		return _time + " " + _uei + " | " + _args;
	}

}
