package com.isoft.struts2.views.tags.ui.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class CommandButtonTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new CommandButton(stack, req, res);
	}	

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		
		CommandButton aui = (CommandButton)component;		
		if(style!=null){
			aui.setStyle(this.getPropertyStringValue(style));
		}
		if(styleClass!=null){
			aui.setStyleClass(this.getPropertyStringValue(styleClass));
		}
		if(onclick!=null){
			aui.setOnclick(onclick);
		}
		if(action!=null){
			aui.setAction(action);
		}
	}
	
	@Override
	public void release() {
		super.release();
		
		this.style = null;
		this.styleClass = null;
		this.onclick = null;
		this.action = null;
	}
	       
	private String style;
	private String styleClass;
	private String onclick;
	private String action;
	
	public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
