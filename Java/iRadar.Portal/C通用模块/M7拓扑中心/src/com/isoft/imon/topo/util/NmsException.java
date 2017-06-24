package com.isoft.imon.topo.util;

public class NmsException extends Exception {
	private static final long serialVersionUID = 1L;

	public NmsException() {
	}

	public NmsException(String message) {
		super(message);
	}

	public NmsException(String message, Throwable cause) {
		super(message, cause);
	}

	public NmsException(Throwable cause) {
		super(cause);
	}
}