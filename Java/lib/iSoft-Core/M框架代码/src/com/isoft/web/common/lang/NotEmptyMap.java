package com.isoft.web.common.lang;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("unchecked")
public class NotEmptyMap extends HashMap{
    private static final long serialVersionUID = 1L;
    private HttpServletRequest request;
    
    public NotEmptyMap(HttpServletRequest request) {
        this.request = request;
    }
    @Override
    public Object put(Object key, Object value) {
        return null;
    }

    @Override
    public boolean containsKey(Object para) {
        return request.getParameter((String)para) != null;
    }

    @Override
    public Object get(Object para) {
        String v = request.getParameter((String)para);
        return v!=null&&v.length()>0;
    }
}
