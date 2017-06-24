package com.isoft.struts2.convert;

public class LongConverter extends Converter {

    @Override
    public Object getAsObject(String[] values) {
        if (values != null) {
            Long[] cvs = new Long[values.length];
            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                if (value.length() > 0) {
                    try {
                        cvs[i] = Long.valueOf(value);
                    } catch (NumberFormatException e) {
                        cvs[i] = new Long(0);
                    }
                }
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
        try {
            return Long.toString(((Number) value).longValue());
        } catch (Exception e) {
            throw new ConverterException(e);
        }
    }

}
