package com.isoft.struts2.convert;

import java.math.BigDecimal;

public class BigDecimalConverter extends Converter {

    @Override
    public Object getAsObject(String[] values) {
        if (values != null) {
            BigDecimal[] cvs = new BigDecimal[values.length];
            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                value = value.replaceAll(",","");
                if (value.length() > 0) {
                    try {
                        cvs[i] = new BigDecimal(value);
                    } catch (NumberFormatException e) {
                        cvs[i] = new BigDecimal(0);
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
            return ((BigDecimal)value).toString();
        } catch (Exception e) {
            throw new ConverterException(e);
        }
    }

}
