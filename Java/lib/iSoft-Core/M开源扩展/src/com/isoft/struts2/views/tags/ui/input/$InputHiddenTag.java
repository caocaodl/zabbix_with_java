package com.isoft.struts2.views.tags.ui.input;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputHiddenTag extends AndurilUITag{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $InputHidden(stack, req, res);
	}
	
    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        $InputHidden aui = ($InputHidden)component;
        aui.setStyleClass(styleClass);
    }
    
    @Override
    public void release() {
        super.release();
        this.styleClass = null;
    }
    
    private String styleClass;

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
    
}
