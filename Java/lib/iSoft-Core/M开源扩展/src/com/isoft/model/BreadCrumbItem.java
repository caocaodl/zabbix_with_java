package com.isoft.model;

import java.io.Serializable;

public class BreadCrumbItem implements Serializable {

	private static final long serialVersionUID = 1L;

    private String _label;

    public BreadCrumbItem(String label) {
        if (label == null)
            throw new NullPointerException("label");
        _label = label;
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

}
