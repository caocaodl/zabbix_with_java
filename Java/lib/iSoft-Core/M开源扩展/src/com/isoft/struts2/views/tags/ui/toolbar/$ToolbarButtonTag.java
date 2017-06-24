package com.isoft.struts2.views.tags.ui.toolbar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $ToolbarButtonTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $ToolbarButton(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$ToolbarButton aui = ($ToolbarButton) component;
		aui.setName(name);
		aui.setOnclick(onclick);
		aui.setIcon(icon);
		aui.setDisabled(getPropertyBooleanValue(disabled));
	}

	@Override
	public void release() {
		super.release();
		this.name = null;
		this.onclick = null;
		this.icon = null;
		this.disabled = null;
	}

	private String name;
	private String icon;
	private String onclick;
	private String disabled;

	public void setName(String name) {
		this.name = name;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

}
