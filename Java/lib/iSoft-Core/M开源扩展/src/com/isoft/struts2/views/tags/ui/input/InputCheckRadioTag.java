package com.isoft.struts2.views.tags.ui.input;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class InputCheckRadioTag extends AndurilUITag{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new InputCheckRadio(stack, req, res);
	}
	
    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        InputCheckRadio tagBean = (InputCheckRadio)component;
        if(this._name != null && this._name.length()>0){
        	tagBean.setName(this._name);
        }
        if(this._onclick != null && this._onclick.length()>0){
        	tagBean.setOnclick(this._onclick);
        }
        if(this._styleClass != null && this._styleClass.length()>0){
        	tagBean.setStyleClass(this._styleClass);
        }
        tagBean.setChecked(this.getPropertyBooleanValue(this._checked));
        tagBean.setDisabled(this.getPropertyBooleanValue(this._disabled));
    }
    
    @Override
    public void release() {
        super.release();
        this._name = null;
        this._onclick = null;
        this._disabled = null;
        this._checked = null;
    }
    private String _name;
    private String _onclick;
    private String _styleClass;
    private String _disabled;
    private String _checked;
    
    public void setName(String name) {
		this._name = name;
	}
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
