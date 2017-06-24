package com.isoft.iradar.exception;

public class RegexpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int code;

	public RegexpException(String message, int code) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
