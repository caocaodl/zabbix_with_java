package com.isoft.struts2.views.tags.ui.block;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $IconBlockTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $IconBlock(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$IconBlock aui = ($IconBlock) component;
		aui.setIcon(icon);
		aui.setWidth(width);
		aui.setIconStyle(iconStyle);
	}

	@Override
	public void release() {
		super.release();
		this.icon = null;
		this.width = null;
		this.iconStyle = null;
	}

	private String icon;
	private String width;
	private String iconStyle;

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setIconStyle(String iconStyle) {
		this.iconStyle = iconStyle;
	}
}
