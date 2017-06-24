package com.isoft.zend.ext.core;

public class ZException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int code;

	public ZException(int code) {
		this(code, "");
	}

	public ZException(int code, String message) {
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
