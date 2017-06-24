package com.isoft.iradar.exception;

public class ExitException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private int code;
	
	public ExitException(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
