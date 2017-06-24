package com.isoft.struts2.views.tags.jquery.easyui.layout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class TabPanelTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new TabPanel(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		TabPanel aui = (TabPanel) component;
		aui.setStyle(style);
	}

	@Override
	public void release() {
		super.release();
		this.style = null;
	}

	private String style;

	public void setStyle(String style) {
		this.style = style;
	}
}
