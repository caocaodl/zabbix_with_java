package com.isoft.web.common.lang;

import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unchecked")
public class RequestParasMap extends HashMap{
    private static final long serialVersionUID = 1L;
    private HttpServletRequest request;
    
    public RequestParasMap(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Object put(Object key, Object value) {
        return null;
    }

    @Override
    public boolean containsKey(Object para) {
        return true;
    }

    @Override
    public Object get(Object para) {
        String[] values = request.getParameterValues((String)para);
        if(values == null){
            values = new String[0];
        }
        return Arrays.asList(values);
    }

}
