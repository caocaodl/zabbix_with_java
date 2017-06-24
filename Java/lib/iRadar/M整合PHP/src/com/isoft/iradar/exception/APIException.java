package com.isoft.iradar.exception;

import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_INTERNAL;

public class APIException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private int code;

	public APIException() {
		this(RDA_API_ERROR_INTERNAL);
	}

	public APIException(int code) {
		this(code, "");
	}
	
	public APIException(String message) {
		super(message);
	}

	public APIException(int code, String message) {
		this(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
