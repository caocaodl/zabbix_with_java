package com.isoft.struts2.views.tags.ui.input;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputTextAreaTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $InputTextArea(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$InputTextArea tagBean = ($InputTextArea) component;
		tagBean.setStyle(_style);
		tagBean.setStyleClass(_styleClass);
		if(_disabled!=null && _disabled.length()>0){
			tagBean.setDisabled(this.getPropertyBooleanValue(_disabled));
		}
		tagBean.setOnblur(_onblur);
		tagBean.setOnchange(_onchange);
		tagBean.setOnfocus(_onfocus);
		tagBean.setOnkeydown(_onkeydown);
		tagBean.setOnkeyup(_onkeyup);
		tagBean.setRows(_rows);
		tagBean.setCols(_cols);
		if(_displayValueOnly!=null && _displayValueOnly.length()>0){
			tagBean.setDisplayValueOnly(this.getPropertyBooleanValue(_displayValueOnly));
		}
	}

	private String _style;
	private String _styleClass;
	private String _disabled;
	private String _onblur;
	private String _onchange;
	private String _onfocus;
	private String _onkeydown;
	private String _onkeyup;
	private String _rows;
	private String _cols;
	private String _displayValueOnly;
	
	public String getOnfocus() {
		return _onfocus;
	}

	public void setOnfocus(String _onfocus) {
		this._onfocus = _onfocus;
	}

	public String getOnchange() {
		return _onchange;
	}

	public void setOnchange(String _onchange) {
		this._onchange = _onchange;
	}

	public String getOnblur() {
		return _onblur;
	}

	public void setOnblur(String _onblur) {
		this._onblur = _onblur;
	}

	public String getDisabled() {
		return _disabled;
	}

	public void setDisabled(String _disabled) {
		this._disabled = _disabled;
	}

	public String getStyleClass() {
		return _styleClass;
	}

	public void setStyleClass(String _styleClass) {
		this._styleClass = _styleClass;
	}

	public String getStyle() {
		return _style;
	}

	public void setStyle(String _style) {
		this._style = _style;
	}

	public String getOnkeydown() {
		return _onkeydown;
	}

	public void setOnkeydown(String _onkeydown) {
		this._onkeydown = _onkeydown;
	}

	public String getOnkeyup() {
		return _onkeyup;
	}

	public void setOnkeyup(String _onkeyup) {
		this._onkeyup = _onkeyup;
	}

	public String getRows() {
		return _rows;
	}

	public void setRows(String _rows) {
		this._rows = _rows;
	}

	public String getCols() {
		return _cols;
	}

	public void setCols(String _cols) {
		this._cols = _cols;
	}
	public String getDisplayValueOnly() {
		return _displayValueOnly;
	}

	public void setDisplayValueOnly(String _displayValueOnly) {
		this._displayValueOnly = _displayValueOnly;
	}
	@Override
	public void release() {
		super.release();
		this._style = null;
		this._styleClass = null;
		this._onblur = null;
		this._onfocus = null;
		this._onkeydown = null;
		this._onkeyup = null;
		this._cols = null;
		this._rows = null;
		this._displayValueOnly = null;
	}

	
}
