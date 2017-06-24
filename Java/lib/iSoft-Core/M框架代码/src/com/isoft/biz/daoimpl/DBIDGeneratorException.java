package com.isoft.biz.daoimpl;

public class DBIDGeneratorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DBIDGeneratorException(final String msg) {
        super(msg);
    }

    public DBIDGeneratorException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
