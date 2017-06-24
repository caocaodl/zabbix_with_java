package com.isoft.struts2.views.tags.ui.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $HeadTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $Head(stack, req, res);
	}

	private String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void release() {
		super.release();
		this.title = null;
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$Head aui = ($Head)component;
		aui.setTitle(title);
	}
	
}
