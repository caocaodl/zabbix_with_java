package com.isoft.struts2.views.tags.ui.select;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class SelectBooleanCheckboxTag extends AndurilUITag{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new SelectBooleanCheckbox(stack, req, res);
	}
	
    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        SelectBooleanCheckbox tagBean = (SelectBooleanCheckbox)component;
        if(this._disabled){
        	tagBean.setDisabled(this._disabled);
        }
        if(this._onclick != null && this._onclick.length()>0){
        	tagBean.setOnclick(this.getPropertyStringValue(this._onclick));
        }
    }
    
    @Override
    public void release() {
        super.release();
        this._disabled = false;
        this._onclick = null;
    }
    
	private boolean _disabled;
    private String _onclick;
    
    public void setDisabled(boolean disabled) {
		this._disabled = disabled;
	}
    public void setOnclick(String onclick) {
		this._onclick = onclick;
	}

}
