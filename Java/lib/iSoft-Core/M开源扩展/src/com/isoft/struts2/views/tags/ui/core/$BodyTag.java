package com.isoft.struts2.views.tags.ui.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $BodyTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	private String _onload;
	private String _onunload;
	private String _oncontextmenu;
	private String _style;

	public String getOnload() {
		return _onload;
	}

	public void setOnload(String onload) {
		this._onload = onload;
	}

	public String getOnunload() {
		return _onunload;
	}

	public void setOnunload(String onunload) {
		this._onunload = onunload;
	}

	public String getOncontextmenu() {
		return _oncontextmenu;
	}

	public void setOncontextmenu(String oncontextmenu) {
		this._oncontextmenu = oncontextmenu;
	}

	public String getStyle() {
		return _style;
	}

	public void setStyle(String style) {
		this._style = style;
	}
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $Body(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$Body tagBean = ($Body) component;
		if (this._onload != null && this._onload.length() > 0) {
			tagBean.setOnload(getPropertyStringValue(this._onload));
		}
		if (this._onunload != null && this._onunload.length() > 0) {
			tagBean.setOnunload(getPropertyStringValue(this._onunload));
		}
		if (this._oncontextmenu != null && this._oncontextmenu.length() > 0) {
			tagBean.setOncontextmenu(getPropertyStringValue(this._oncontextmenu));
		}
		if (this._style != null && this._style.length() > 0) {
			tagBean.setStyle(getPropertyStringValue(this._style));
		}
	}

	@Override
	public void release() {
		super.release();
		this._onload = null;
		this._onunload = null;
		this._oncontextmenu = null;
		this._style = null;
	}
}
