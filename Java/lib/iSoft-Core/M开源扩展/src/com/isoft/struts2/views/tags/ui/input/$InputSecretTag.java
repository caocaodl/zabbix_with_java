package com.isoft.struts2.views.tags.ui.input;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputSecretTag extends AndurilUITag {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        return new $InputSecret(stack, req, res);
    }

    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        $InputSecret sec = ($InputSecret) component;
        if (style != null) {
        	sec.setStyle(style);
		}
		if (styleClass != null) {
			sec.setStyleClass(this.getPropertyStringValue(styleClass));
		}
		if (maxlength != null) {
			sec.setMaxlength(this.getPropertyIntegerValue(maxlength));
		}
		if (onblur != null) {
			sec.setOnblur(onblur);
		}
		if (onchange != null) {
			sec.setOnchange(onchange);
		}
    }
    
    
    
    @Override
    public void release() {
        super.release();
        this.style = null;
        this.styleClass = null;
        this.maxlength = null;
        this.onblur = null;
        this.onchange = null;
    }
    
    private String style = null;
    private String styleClass = null;
    private String maxlength = null;
    private String onblur = null;
    private String onchange = null;
    
    
    public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public void setOnblur(String onblur) {
		this.onblur = onblur;
	}

}
