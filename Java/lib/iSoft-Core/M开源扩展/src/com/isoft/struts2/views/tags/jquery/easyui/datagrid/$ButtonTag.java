package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilTagSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class $ButtonTag extends AndurilTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $Button(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$Button aui = ($Button) component;
		ButtonItem item = new ButtonItem();
		item.setCaption(this.caption);
		item.setIcon(this.icon);
		item.setOnClick(this.onClick);
		aui.setModel(item);
	}

	@Override
	public void release() {
		super.release();
		this.caption = null;
		this.icon = null;
		this.onClick = null;
	}

	private String caption;
	private String icon;
	private String onClick;

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

}
