package com.isoft.struts2.convert;

import org.apache.struts2.StrutsException;

public class ConverterException extends StrutsException{

    private static final long serialVersionUID = 1L;

    public ConverterException() {
        super();
    }

    public ConverterException(String s, Object target) {
        super(s, target);
    }

    public ConverterException(String s, Throwable cause, Object target) {
        super(s, cause, target);
    }

    public ConverterException(String s, Throwable cause) {
        super(s, cause);
    }

    public ConverterException(String s) {
        super(s);
    }

    public ConverterException(Throwable cause, Object target) {
        super(cause, target);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }   

}
