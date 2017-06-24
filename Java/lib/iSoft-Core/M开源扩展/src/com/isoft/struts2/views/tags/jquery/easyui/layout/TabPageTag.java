package com.isoft.struts2.views.tags.jquery.easyui.layout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class TabPageTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new TabPage(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		TabPage aui = (TabPage) component;
		aui.setTitle(title);
		aui.setStyle(style);
	}

	@Override
	public void release() {
		super.release();
		this.title = null;
		this.style = null;
	}

	private String title;
	private String style;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
