package com.isoft.struts2.views.tags.ui.linkButton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class ParamTag extends AndurilUITag {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        return new Param(stack, req, res);
    }

    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        Param tagBean = (Param)component;
        if(name != null){
        	tagBean.setName(name);
        }
    }
    
    @Override
    public void release() {
        super.release();
        this.name = null;
    }
    
    private String name;

	public void setName(String name) {
		this.name = name;
	}
}
