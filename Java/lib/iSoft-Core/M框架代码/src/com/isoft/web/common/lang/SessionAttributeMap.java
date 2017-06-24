package com.isoft.web.common.lang;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

@SuppressWarnings("unchecked")
public class SessionAttributeMap extends HashMap{
    private static final long serialVersionUID = 1L;
    private HttpSession session;
    public SessionAttributeMap(HttpSession session) {
        this.session = session;
    }

    @Override
    public Object put(Object key, Object value) {
        return null;
    }

    @Override
    public boolean containsKey(Object para) {
        return this.session.getAttribute((String)para) != null;
    }

    @Override
    public Object get(Object key) {
        return this.session.getAttribute((String)key);
    }
}
