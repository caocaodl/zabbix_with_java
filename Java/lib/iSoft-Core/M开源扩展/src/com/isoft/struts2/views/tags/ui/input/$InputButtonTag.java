package com.isoft.struts2.views.tags.ui.input;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputButtonTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $InputButton(stack, req, res);
	}

	private String _onclick;
	private String _disabled;
	private String _style;
	private String _styleClass;

	public String getDisabled() {
		return _disabled;
	}

	public void setDisabled(String _disabled) {
		this._disabled = _disabled;
	}

	public String getOnclick() {
		return _onclick;
	}

	public void setOnclick(String _onclick) {
		this._onclick = _onclick;
	}

	public void setStyle(String style) {
		this._style = style;
	}

	public void setStyleClass(String styleClass) {
		this._styleClass = styleClass;
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$InputButton tagBean = ($InputButton) component;
		if (_disabled != null && _disabled.length() > 0) {
            tagBean.setDisabled(this.getPropertyBooleanValue(_disabled));
        }
		tagBean.setOnclick(_onclick);
		tagBean.setStyle(_style);
		tagBean.setStyleClass(_styleClass);
	}

	@Override
	public void release() {
		super.release();
		this._disabled = null;
		this._onclick = null;
		this._style = null;
		this._styleClass = null;
	}
}
