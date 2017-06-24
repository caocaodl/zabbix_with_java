package com.isoft.struts2.views.tags.ui.input;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputTextTag extends AndurilUITag {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        return new $InputText(stack, req, res);
    }

    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        $InputText aui = ($InputText)component;
        
        if (style != null) {
			aui.setStyle(style);
		}
		
		if (styleClass != null) {
			aui.setStyleClass(this.getPropertyStringValue(styleClass));
		}
		if (onclick != null) {
			aui.setOnclick(onclick);
		}
		
		if (onkeypress != null) {
			aui.setOnkeypress(onkeypress);
		}
		if (onkeydown != null) {
			aui.setOnkeydown(onkeydown);
		}
		if (onkeyup != null) {
			aui.setOnkeyup(onkeyup);
		}
		if (align != null) {
			aui.setAlign(this.getPropertyStringValue(align));
		}
		if (disabled != null) {
			aui.setDisabled(this.getPropertyBooleanValue(disabled));
		}
		if (maxlength != null) {
			aui.setMaxlength(this.getPropertyIntegerValue(maxlength));
		}
		if (onblur != null) {
			aui.setOnblur(onblur);
		}
		if (onchange != null) {
			aui.setOnchange(onchange);
		}
		if (onfocus != null) {
			aui.setOnfocus(onfocus);
		}
		if (readonly != null) {
			aui.setReadonly(this.getPropertyBooleanValue(readonly));
		}
		if (size != null) {
			aui.setSize(this.getPropertyIntegerValue(size));
		}
        
        if(displayValueOnly != null) {
        	aui.setDisplayValueOnly(this.getPropertyBooleanValue(displayValueOnly));
        }
    }
    
    @Override
    public void release() {
        super.release();
        
        this.style = null;
        this.styleClass = null;
        this.onclick = null;
        this.onkeypress = null;
        this.onkeydown = null;
        this.onkeyup = null;
        this.align = null;
        this.disabled = null;
        this.maxlength = null;
        this.onblur = null;
        this.onchange = null;
        this.onfocus = null;
        this.readonly = null;
        this.size = null;
        
        this.displayValueOnly = null;
    }
    
    private String style = null;
    private String styleClass = null;
    
    //HTML 4.0 event-handler attributes
    private String onclick = null;
    private String onkeypress = null;
    private String onkeydown = null;
    private String onkeyup = null;
    
    //HTML 4.0 input attributes
    private String align = null;
    private String disabled = null;
    private String maxlength = null;
    private String onblur = null;
    private String onchange = null;
    private String onfocus = null;
    private String readonly = null;
    private String size = null;
    
    private String displayValueOnly;
    
    
    public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public void setOnkeypress(String onkeypress) {
		this.onkeypress = onkeypress;
	}

	public void setOnkeydown(String onkeydown) {
		this.onkeydown = onkeydown;
	}

	public void setOnkeyup(String onkeyup) {
		this.onkeyup = onkeyup;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public void setOnblur(String onblur) {
		this.onblur = onblur;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public void setOnfocus(String onfocus) {
		this.onfocus = onfocus;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public void setSize(String size) {
		this.size = size;
	}
    
	public void setDisplayValueOnly(String displayValueOnly) {
		this.displayValueOnly = displayValueOnly;
	}
}
