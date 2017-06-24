package com.isoft.biz.dto;

public class DelegateDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	private Object objParam;
	private Exception exception;

	public Object getObjParam() {
		return objParam;
	}

	public void setObjParam(Object objParam) {
		this.objParam = objParam;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public boolean hasException() {
		return this.exception != null;
	}

}
