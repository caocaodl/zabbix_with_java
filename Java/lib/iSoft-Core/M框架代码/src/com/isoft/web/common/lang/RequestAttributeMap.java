package com.isoft.web.common.lang;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unchecked")
public class RequestAttributeMap extends HashMap{
    private static final long serialVersionUID = 1L;
    private HttpServletRequest request;
    
    public RequestAttributeMap(HttpServletRequest request) {
        this.request = request;
    }
    @Override
    public Object put(Object key, Object value) {
        return null;
    }

    @Override
    public boolean containsKey(Object para) {
        return request.getAttribute((String)para) != null;
    }

    @Override
    public Object get(Object key) {
        return request.getAttribute((String)key);
    }
}
