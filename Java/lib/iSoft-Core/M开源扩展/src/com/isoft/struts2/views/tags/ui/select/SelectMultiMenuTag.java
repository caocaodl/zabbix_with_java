package com.isoft.struts2.views.tags.ui.select;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class SelectMultiMenuTag extends AndurilUITag{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new SelectMultiMenu(stack, req, res);
	}
	
    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        SelectMultiMenu tagBean = (SelectMultiMenu)component;
        if(this._disabled){
        	tagBean.setDisabled(this._disabled);
        }
        if(this._displayValueOnly){
        	tagBean.setDisplayValueOnly(this._displayValueOnly);
        }
        if(this._onchange != null && this._onchange.length()>0){
        	tagBean.setOnchange(this.getPropertyStringValue(this._onchange));
        }
        if(this._style != null && this._style.length()>0){
        	tagBean.setStyle(this.getPropertyStringValue(this._style));
        }
        if(this._styleClass != null && this._styleClass.length()>0){
        	tagBean.setStyleClass(this.getPropertyStringValue(this._styleClass));
        }
    }
    
    @Override
    public void release() {
        super.release();
        this._disabled = false;
        this._displayValueOnly = false;
    }
    
    private boolean _disabled;
    private boolean _displayValueOnly;
    private String _onchange;
    private String _style;
    private String _styleClass;
    
    
    public void setDisabled(boolean disabled) {
		this._disabled = disabled;
	}
    public void setDisplayValueOnly(boolean displayValueOnly) {
		_displayValueOnly = displayValueOnly;
	}
    public void setOnchange(String onchange) {
		this._onchange = onchange;
	}
    public void setStyle(String style) {
		this._style = style;
	}
    public void setStyleClass(String styleClass) {
		this._styleClass = styleClass;
	}
}
