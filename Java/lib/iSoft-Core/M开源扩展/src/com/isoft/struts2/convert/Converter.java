package com.isoft.struts2.convert;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;

public abstract class Converter {
    protected String id;
    protected Class<?> objClass;
    
    protected OgnlUtil ognlUtil;
    
    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<?> getObjClass() {
        return objClass;
    }

    public void setObjClass(Class<?> objClass) {
        this.objClass = objClass;
    }

    public abstract Object getAsObject(String[] values) throws ConverterException;

    public abstract String getAsString(Object value) throws ConverterException;
}
