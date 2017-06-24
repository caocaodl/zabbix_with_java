package com.isoft.struts2.views.tags.ui.input;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class InputCheckBoxTag extends AndurilUITag{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new InputCheckBox(stack, req, res);
	}

	@Override
    protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		InputCheckBox aui = (InputCheckBox)component;
		if(this._onclick != null && this._onclick.length()>0){
        	aui.setOnclick(this._onclick);
        }
		if(this._styleClass != null && this._styleClass.length()>0){
        	aui.setStyleClass(this._styleClass);
        }
		if (this._disabled != null && this._disabled.length()>0) {
			aui.setDisabled(getPropertyBooleanValue(this._disabled));
		}
		if (this._checked != null && this._checked.length()>0) {
			aui.setChecked(getPropertyBooleanValue(this._checked));
		}
	}
	
	@Override
    public void release() {
        super.release();
		this._onclick = null;
		this._styleClass = null;
		this._disabled = null;
		this._checked = null;
	}
	
	private String _onclick;
	private String _styleClass;
    private String _disabled;
    private String _checked;

    public void setOnclick(String onclick) {
		this._onclick = onclick;
	}
    
    public void setStyleClass(String styleClass) {
		_styleClass = styleClass;
	}
    
	public void setDisabled(String disabled) {
		this._disabled = disabled;
	}

	public void setChecked(String checked) {
		this._checked = checked;
	}
}
