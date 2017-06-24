package com.isoft.web.common.lang;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class MaxLengthMap extends HashMap{
    private static final long serialVersionUID = 1L;
    public MaxLengthMap(int initialCapacity) {
        super(initialCapacity);
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
        //return ValidationCfg.getMaxLength().getValidation((String)para);
    	return null;
    }
}
