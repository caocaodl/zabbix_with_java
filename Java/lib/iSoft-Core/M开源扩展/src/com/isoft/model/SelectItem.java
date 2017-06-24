package com.isoft.model;

import java.io.Serializable;

public class SelectItem implements Serializable {
    private static final long serialVersionUID = 1L;
    // FIELDS
    private Object _value;
    private String _label;
    private String _description;
    private boolean _disabled;

    // CONSTRUCTORS
    public SelectItem() {
    }

    public SelectItem(Object value) {
        if (value == null)
            throw new NullPointerException("value");
        _value = value;
        _label = value.toString();
        _description = null;
        _disabled = false;
    }

    public SelectItem(Object value, String label) {
        if (value == null)
            throw new NullPointerException("value");
        if (label == null)
            throw new NullPointerException("label");
        _value = value;
        _label = label;
        _description = null;
        _disabled = false;
    }

    public SelectItem(Object value, String label, String description) {
        if (value == null)
            throw new NullPointerException("value");
        if (label == null)
            throw new NullPointerException("label");
        _value = value;
        _label = label;
        _description = description;
        _disabled = false;
    }

    public SelectItem(Object value, String label, String description,
            boolean disabled) {
        if (value == null)
            throw new NullPointerException("value");
        if (label == null)
            throw new NullPointerException("label");
        _value = value;
        _label = label;
        _description = description;
        _disabled = disabled;
    }

    // METHODS
    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public boolean isDisabled() {
        return _disabled;
    }

    public void setDisabled(boolean disabled) {
        _disabled = disabled;
    }

    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        if (label == null) {
            label = "";
        }
        _label = label;
    }

    public final Object getValue() {
        return _value;
    }

    public final void setValue(final Object value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        _value = value;
    }
}
