package com.isoft.web.common.lang;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class DateTimeMap extends HashMap{
    private static final long serialVersionUID = 1L;
    
    public DateTimeMap() {
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
        if("today".equals(para)){
            return new Date();
        }else if("month".equals(para)){
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),1,0,0,0);
            return calendar.getTime();
        }else if("year".equals(para)){
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY,1,0,0,0);
            return calendar.getTime();
        }
        return null;
    }
}
