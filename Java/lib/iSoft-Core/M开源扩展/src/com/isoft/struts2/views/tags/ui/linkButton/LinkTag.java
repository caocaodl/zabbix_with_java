package com.isoft.struts2.views.tags.ui.linkButton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class LinkTag extends AndurilUITag {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        return new Link(stack, req, res);
    }

    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        Link tagBean = (Link)component;
        if(this.onclick != null){
        	tagBean.setOnclick(onclick);
        }
    }
    
    @Override
    public void release() {
        super.release();
        this.onclick = null;
    }
    
    private String onclick;

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
}
