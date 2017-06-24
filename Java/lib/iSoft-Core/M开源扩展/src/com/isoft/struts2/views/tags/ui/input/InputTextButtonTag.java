package com.isoft.struts2.views.tags.ui.input;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class InputTextButtonTag extends AndurilUITag{

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new InputTextButton(stack,req,res);
	}
	private String _onButtonClick;
	private String _onkeydown;
	private String _style;
	private String _value;

	public String getOnButtonClick() {
		return _onButtonClick;
	}

	public void setOnButtonClick(String buttonClick) {
		_onButtonClick = buttonClick;
	}

	public String getOnkeydown() {
		return _onkeydown;
	}

	public void setOnkeydown(String _onkeydown) {
		this._onkeydown = _onkeydown;
	}

	public String getStyle() {
		return _style;
	}

	public void setStyle(String _style) {
		this._style = _style;
	}

	public String getValue() {
		return _value;
	}

	@Override
    public void setValue(String _value) {
		this._value = _value;
	}
	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		InputTextButton tagbean = (InputTextButton) component;
		tagbean.setOnButtonClick(_onButtonClick);
		tagbean.setOnkeydown(_onkeydown);
		tagbean.setStyle(_style);
		tagbean.setValue(_value);
	}
	@Override
	public void release() {
		super.release();
		this._value = null;
		this._style = null;
		this._onButtonClick = null;
		this._onkeydown = null;
	}
}
