package com.isoft.struts2.convert;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import com.isoft.util.LocaleUtil;

public class NumberConverter extends Converter {
    private String _currencyCode;
    private String _currencySymbol;
    private Locale _locale;
    private int _maxFractionDigits;
    private int _maxIntegerDigits;
    private int _minFractionDigits;
    private int _minIntegerDigits;
    private String _pattern;
    private String _type = "number";
    private boolean _groupingUsed = true;
    private boolean _integerOnly = false;
    private boolean _transient;

    private boolean _maxFractionDigitsSet;
    private boolean _maxIntegerDigitsSet;
    private boolean _minFractionDigitsSet;
    private boolean _minIntegerDigitsSet;

    @Override
    public Object getAsObject(String[] values) {
        if (values != null) {
            Number[] cvs = new Number[values.length];
            for (int i = 0; i < cvs.length; i++) {
                String value = values[i].trim();
                if (value.length() > 0) {
                    NumberFormat format = getNumberFormat();
                    format.setParseIntegerOnly(_integerOnly);
                    try {
                        cvs[i] = format.parse(value);
                    } catch (ParseException e) {
                        try {
                            cvs[i] = format.parse("0");
                        } catch (ParseException e1) {
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

        NumberFormat format = getNumberFormat();
        format.setGroupingUsed(_groupingUsed);
        if (_maxFractionDigitsSet)
            format.setMaximumFractionDigits(_maxFractionDigits);
        if (_maxIntegerDigitsSet)
            format.setMaximumIntegerDigits(_maxIntegerDigits);
        if (_minFractionDigitsSet)
            format.setMinimumFractionDigits(_minFractionDigits);
        if (_minIntegerDigitsSet)
            format.setMinimumIntegerDigits(_minIntegerDigits);
        formatCurrency(format);
        try {
            return format.format(value);
        } catch (Exception e) {
            throw new ConverterException("Cannot convert value '" + value + "'");
        }
    }

    private NumberFormat getNumberFormat() {
        Locale lokale = _locale != null ? _locale : LocaleUtil.getLocale();

        if (_pattern == null && _type == null) {
            throw new ConverterException(
                    "Cannot get NumberFormat, either type or pattern needed.");
        }

        // pattern
        if (_pattern != null) {
            return new DecimalFormat(_pattern, new DecimalFormatSymbols(lokale));
        }

        // type
        if (_type.equals("number")) {
            return NumberFormat.getNumberInstance(lokale);
        } else if (_type.equals("currency")) {
            return NumberFormat.getCurrencyInstance(lokale);
        } else if (_type.equals("percent")) {
            return NumberFormat.getPercentInstance(lokale);
        }
        throw new ConverterException("Cannot get NumberFormat, illegal type "
                + _type);
    }

    private void formatCurrency(NumberFormat format) {
        if (_currencyCode == null && _currencySymbol == null) {
            return;
        }

        boolean useCurrencyCode = _currencySymbol == null;

        if (useCurrencyCode) {
            // set Currency
            try {
                format.setCurrency(Currency.getInstance(_currencyCode));
            } catch (Exception e) {
                throw new ConverterException(
                        "Unable to get Currency instance for currencyCode "
                                + _currencyCode);
            }
        } else if (format instanceof DecimalFormat) {
            DecimalFormat dFormat = (DecimalFormat) format;
            DecimalFormatSymbols symbols = dFormat.getDecimalFormatSymbols();
            symbols.setCurrencySymbol(_currencySymbol);
            dFormat.setDecimalFormatSymbols(symbols);
        }
    }

    // GETTER & SETTER
    public String getCurrencyCode() {
        return _currencyCode != null ? _currencyCode
                : getDecimalFormatSymbols().getInternationalCurrencySymbol();
    }

    public void setCurrencyCode(String currencyCode) {
        _currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return _currencySymbol != null ? _currencySymbol
                : getDecimalFormatSymbols().getCurrencySymbol();
    }

    public void setCurrencySymbol(String currencySymbol) {
        _currencySymbol = currencySymbol;
    }

    public boolean isGroupingUsed() {
        return _groupingUsed;
    }

    public void setGroupingUsed(boolean groupingUsed) {
        _groupingUsed = groupingUsed;
    }

    public boolean isIntegerOnly() {
        return _integerOnly;
    }

    public void setIntegerOnly(boolean integerOnly) {
        _integerOnly = integerOnly;
    }

    public Locale getLocale() {
        if (_locale != null)
            return _locale;
        return LocaleUtil.getLocale();
    }

    public void setLocale(Locale locale) {
        _locale = locale;
    }

    public int getMaxFractionDigits() {
        return _maxFractionDigits;
    }

    public void setMaxFractionDigits(int maxFractionDigits) {
        _maxFractionDigitsSet = true;
        _maxFractionDigits = maxFractionDigits;
    }

    public int getMaxIntegerDigits() {
        return _maxIntegerDigits;
    }

    public void setMaxIntegerDigits(int maxIntegerDigits) {
        _maxIntegerDigitsSet = true;
        _maxIntegerDigits = maxIntegerDigits;
    }

    public int getMinFractionDigits() {
        return _minFractionDigits;
    }

    public void setMinFractionDigits(int minFractionDigits) {
        _minFractionDigitsSet = true;
        _minFractionDigits = minFractionDigits;
    }

    public int getMinIntegerDigits() {
        return _minIntegerDigits;
    }

    public void setMinIntegerDigits(int minIntegerDigits) {
        _minIntegerDigitsSet = true;
        _minIntegerDigits = minIntegerDigits;
    }

    public String getPattern() {
        return _pattern;
    }

    public void setPattern(String pattern) {
        _pattern = pattern;
    }

    public boolean isTransient() {
        return _transient;
    }

    public void setTransient(boolean aTransient) {
        _transient = aTransient;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    private DecimalFormatSymbols getDecimalFormatSymbols() {
        return new DecimalFormatSymbols(getLocale());
    }
}
