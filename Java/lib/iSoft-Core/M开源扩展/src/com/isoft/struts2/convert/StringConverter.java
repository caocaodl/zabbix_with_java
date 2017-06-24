package com.isoft.struts2.convert;

public class StringConverter extends Converter {
    
    @Override
    public Object getAsObject(String[] values) {
        if (values != null) {
            String[] cvs = new String[values.length];
            for(int i=0;i<cvs.length;i++){
                cvs[i] = values[i].trim();
            }
            return cvs.length>1?cvs:cvs[0];
        }
        return null;
    }

    @Override
    public String getAsString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

}
