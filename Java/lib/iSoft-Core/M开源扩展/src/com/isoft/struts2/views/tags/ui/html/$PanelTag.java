package com.isoft.struts2.views.tags.ui.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $PanelTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $Panel(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$Panel aui = ($Panel) component;
		aui.setTitle(title);
		aui.setWidth(width);
		aui.setStyle(style);
		aui.setWidgetstyle(widgetstyle);
		aui.setIconClass(iconClass);
		aui.setClosable(getPropertyBooleanValue(closable));
		aui.setCollapsible(getPropertyBooleanValue(collapsible));
		aui.setMinimizable(getPropertyBooleanValue(minimizable));
		aui.setMaximizable(getPropertyBooleanValue(maximizable));
	}

	@Override
	public void release() {
		super.release();
		this.title = null;
		this.width = null;
		this.style = null;
		this.widgetstyle = null;
		this.iconClass = null;
		this.closable = null;
		this.collapsible = null;
		this.minimizable = null;
		this.maximizable = null;
	}

	private String title;
	private String width;
	private String style;
	private String widgetstyle;
	private String iconClass;
	private String closable;
	private String collapsible;
	private String minimizable;
	private String maximizable;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setWidgetstyle(String widgetstyle) {
		this.widgetstyle = widgetstyle;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

	public void setClosable(String closable) {
		this.closable = closable;
	}

	public void setCollapsible(String collapsible) {
		this.collapsible = collapsible;
	}

	public void setMinimizable(String minimizable) {
		this.minimizable = minimizable;
	}

	public void setMaximizable(String maximizable) {
		this.maximizable = maximizable;
	}

}
