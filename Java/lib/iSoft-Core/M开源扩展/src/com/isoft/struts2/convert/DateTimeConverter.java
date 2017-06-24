package com.isoft.struts2.convert;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.isoft.util.LocaleUtil;

public class DateTimeConverter extends Converter {
    
    private static final String TYPE_DATE = "date";
    private static final String TYPE_TIME = "time";
    private static final String TYPE_BOTH = "both";
    private static final String STYLE_DEFAULT = "default";
    private static final String STYLE_MEDIUM = "medium";
    private static final String STYLE_SHORT = "short";
    private static final String STYLE_LONG = "long";
    private static final String STYLE_FULL = "full";

    private String _dateStyle;
    private Locale _locale;
    private String _pattern;
    private String _timeStyle;
    private String _type;
    private boolean _transient;

    @Override
    public Object getAsObject(String[] values) {
        if (values != null) {
            Date[] cvs = new Date[values.length];
            for (int i = 0; i < cvs.length; i++) {
                String value = values[i].trim();
                if (value != null) {
                    value = value.trim();
                    if (value.length() > 0) {
                        DateFormat format = getDateFormat();
                        try {
                            cvs[i] = format.parse(value);
                        } catch (ParseException e) {
                            cvs[i] = new Date();
                        }
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

        DateFormat format = getDateFormat();
        try {
            return format.format(value);
        } catch (Exception e) {
            throw new ConverterException("Cannot convert value '" + value + "'");
        }
    }
    
    public Date getAsDate(Object value) {
    	if (value != null) {
    		if(value instanceof Date) {
    			return (Date)value;
    		}else if(value instanceof String){
    			String sv = (String)value;
    			if(sv.length() > 0) {
    				DateFormat format = getDateFormat();
    				Date d = null;
        			try {
        				d = format.parse(sv);
        			}catch (ParseException e) {}
        			return d;
    			}
    		}
        }
        return null;
    }

    private DateFormat getDateFormat() {
        String type = getType();
        DateFormat format = null;
        if (_pattern != null) {
            try {
                format = new SimpleDateFormat(_pattern, getLocale());
            } catch (IllegalArgumentException iae) {
                throw new ConverterException("Invalid pattern", iae);
            }
        } else if (type.equals(TYPE_DATE)) {
            format = DateFormat.getDateInstance(calcStyle(getDateStyle()),
                    getLocale());
        } else if (type.equals(TYPE_TIME)) {
            format = DateFormat.getTimeInstance(calcStyle(getTimeStyle()),
                    getLocale());
        } else if (type.equals(TYPE_BOTH)) {
            format = DateFormat.getDateTimeInstance(calcStyle(getDateStyle()),
                    calcStyle(getTimeStyle()), getLocale());
        } else {
            throw new ConverterException("invalid type '" + _type + "'");
        }

        // format cannot be lenient (JSR-127)
        format.setLenient(false);
        return format;
    }

    private int calcStyle(String name) {
        if (name.equals(STYLE_DEFAULT)) {
            return DateFormat.DEFAULT;
        }
        if (name.equals(STYLE_MEDIUM)) {
            return DateFormat.MEDIUM;
        }
        if (name.equals(STYLE_SHORT)) {
            return DateFormat.SHORT;
        }
        if (name.equals(STYLE_LONG)) {
            return DateFormat.LONG;
        }
        if (name.equals(STYLE_FULL)) {
            return DateFormat.FULL;
        }

        throw new ConverterException("invalid style '" + name + "'");
    }

    // GETTER & SETTER
    public String getDateStyle() {
        return _dateStyle != null ? _dateStyle : STYLE_DEFAULT;
    }

    public void setDateStyle(String dateStyle) {
        // TODO: validate timeStyle
        _dateStyle = dateStyle;
    }

    public Locale getLocale() {
        if (_locale != null)
            return _locale;
        return LocaleUtil.getLocale();
    }

    public void setLocale(Locale locale) {
        _locale = locale;
    }

    public String getPattern() {
        return _pattern;
    }

    public void setPattern(String pattern) {
        _pattern = pattern;
    }

    public String getTimeStyle() {
        return _timeStyle != null ? _timeStyle : STYLE_DEFAULT;
    }

    public void setTimeStyle(String timeStyle) {
        // TODO: validate timeStyle
        _timeStyle = timeStyle;
    }

    public boolean isTransient() {
        return _transient;
    }

    public void setTransient(boolean aTransient) {
        _transient = aTransient;
    }

    public String getType() {
        return _type != null ? _type : TYPE_DATE;
    }

    public void setType(String type) {
        // TODO: validate type
        _type = type;
    }
}
