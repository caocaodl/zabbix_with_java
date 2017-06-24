package com.isoft.struts2.convert;

import java.math.BigInteger;

public class BigIntegerConverter extends Converter {

    @Override
    public Object getAsObject(String[] values) {
        if (values != null) {
            BigInteger[] cvs = new BigInteger[values.length];
            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                if (value.length() > 0) {
                    try {
                        cvs[i] = new BigInteger(value);
                    } catch (NumberFormatException e) {
                        cvs[i] = BigInteger.valueOf(0);
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
            return ((BigInteger)value).toString();
        } catch (Exception e) {
            throw new ConverterException(e);
        }
    }

}
