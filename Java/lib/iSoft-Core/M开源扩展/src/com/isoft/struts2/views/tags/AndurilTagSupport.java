package com.isoft.struts2.views.tags;

import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.util.UIComponentTagUtils;
import com.opensymphony.xwork2.util.TextParseUtil;

public abstract class AndurilTagSupport extends ComponentTagSupport {

	private static final long serialVersionUID = 1L;
	private String _value;
    private String _rendered = null;
    
    @Override
    protected void populateParams() {
        setProperties((AndurilComponent)this.component);
    }

    protected void setProperties(AndurilComponent component) {
        if (_rendered != null) {
            component.setRendered(getPropertyBooleanValue(_rendered));
        }
        if (_value != null) {
            setValueProperty(component, _value);
        }
    }
    

    @Override
    public void release() {
        this._rendered = null;
        this._value=null;
    }

    public void setValue(String value) {
        _value = value;
    }
    
    public void setRendered(String _rendered) {
        this._rendered = _rendered;
    }

    protected boolean isValueReference(String vb) {
        return UIComponentTagUtils.isValueReference(vb);
    } 
    
    protected String getValueBindingExpr(String vb) {
        return UIComponentTagUtils.getValueBindingExpr(vb);
    }
    
    protected String getPropertyStringValue(String property) {
    	return getPropertyStringValue(property, null);
    }
    
    protected String getPropertyStringValue(String property, String defaultValue) {
        if (property != null) {
            if (isValueReference(property)) {
                String expr = getValueBindingExpr(property);
                return (String) findValue(expr, String.class);
            } else {
                return property;
            }
        } else {
            return defaultValue;
        }
    }
    
    protected Boolean getPropertyBooleanValue(String property) {
    	return getPropertyBooleanValue(property, false);
    }
    
    protected Boolean getPropertyBooleanValue(String property, Boolean defaultValue) {
        if (property != null) {
            if (isValueReference(property)) {
                String expr = getValueBindingExpr(property);
                Boolean answer = (Boolean) findValue(expr, Boolean.class);
                if (answer == null) {
                    answer = Boolean.FALSE;
                }
                return answer;
            } else {
                return Boolean.valueOf(property);
            }
        } else {
            return defaultValue;
        }
    }
    
    protected Integer getPropertyIntegerValue(String property) {
        return getPropertyIntegerValue(property, null);
    }
    
    protected Integer getPropertyIntegerValue(String property,Integer defaultValue) {
        if (property != null) {
            if (isValueReference(property)) {
                String expr = getValueBindingExpr(property);
                return (Integer) findValue(expr, Integer.class);
            } else {
                return Integer.valueOf(property);
            }
        } else {
            return defaultValue;
        }
    }
    
    protected Object getPropertyObjectValue(String property) {
        if (property != null) {
            if (isValueReference(property)) {
                String expr = getValueBindingExpr(property);
                return findValue(expr, String.class);
            } else {
                return property;
            }
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Object findValue(String expr, Class toType) {
        if (this.component.altSyntax() && toType == String.class) {
            return TextParseUtil.translateVariables('%', expr, getStack());
        } else {
            return getStack().findValue(expr, toType);
        }
    }
    
    private void setValueProperty(AndurilComponent component, String value) {
        UIComponentTagUtils.setValueProperty(component, value);
    }

    protected void setValueBinding(AndurilComponent component, String propName,
            String value) {
        UIComponentTagUtils.setValueBinding(component, propName, value);
    }
}
