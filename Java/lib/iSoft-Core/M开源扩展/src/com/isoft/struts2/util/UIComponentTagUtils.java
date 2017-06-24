package com.isoft.struts2.util;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HTML;

public class UIComponentTagUtils {

    public static boolean isValueReference(String value) {
        if (value == null)
            throw new NullPointerException("value");
        
        return value.startsWith("#{") && value.endsWith("}");        
    }

    public static String getValueBindingExpr(String vb) {
        return vb.substring(2, vb.length() - 1);
    }

    public static void setValueProperty(AndurilComponent component, String value) {
        if (value != null) {
            if (isValueReference(value)) {
                ValueBinding vb = createValueBinding(value);
                component.setValueBinding(HTML.VALUE_ATTR, vb);
            } else {
                component.setValue(value);
            }
        }
    }

    public static void setValueBinding(AndurilComponent component,
            String propName, String value) {
        if (value != null) {
            if (isValueReference(value)) {
                ValueBinding vb = createValueBinding(value);
                component.setValueBinding(propName, vb);
            } else {
                throw new IllegalArgumentException("Attribute " + propName
                        + " must be a value reference");
            }
        }
    }
    
    public static ValueBinding createValueBinding(String expr) {
        return new ValueBinding(getValueBindingExpr(expr));
    }

}
