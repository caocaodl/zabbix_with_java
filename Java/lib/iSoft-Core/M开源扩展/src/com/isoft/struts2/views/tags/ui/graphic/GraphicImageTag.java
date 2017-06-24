package com.isoft.struts2.views.tags.ui.graphic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class GraphicImageTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new GraphicImage(stack, req, res);
	}

	private String _border;
	private String _style;
	private String _styleClass;
	private String _title;
	private String _width;
	private String _height;
	private String _alt;
	private String _onclick;

	public String getBorder() {
		return _border;
	}

	public void setBorder(String _border) {
		this._border = _border;
	}
	
	public String getStyle() {
		return _style;
	}

	public void setStyle(String _style) {
		this._style = _style;
	}

	public String getStyleClass() {
		return _styleClass;
	}

	public void setStyleClass(String class1) {
		_styleClass = class1;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String _title) {
		this._title = _title;
	}
	
	public String getWidth() {
		return _width;
	}

	public void setWidth(String width) {
		this._width = width;
	}

	public String getHeight() {
		return _height;
	}

	public void setHeight(String height) {
		this._height = height;
	}

	public String getAlt() {
		return _alt;
	}

	public void setAlt(String alt) {
		this._alt = alt;
	}
	
	public String getOnclick() {
		return _onclick;
	}

	public void setOnclick(String _onclick) {
		this._onclick = _onclick;
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		GraphicImage tagBean = (GraphicImage) component;
		tagBean.setBorder(getPropertyStringValue(_border));
		tagBean.setStyleClass(getPropertyStringValue(_styleClass));
		tagBean.setTitle(getPropertyStringValue(_title));
		tagBean.setWidth(getPropertyStringValue(_width));
		tagBean.setHeight(getPropertyStringValue(_height));
		tagBean.setAlt(getPropertyStringValue(_alt));
		tagBean.setStyle(getPropertyStringValue(_style));
		tagBean.setOnclick(getPropertyStringValue(_onclick));
	}

	@Override
	public void release() {
		super.release();
		this._border = null;
		this._style = null;
		this._styleClass = null;
		this._title = null;
		this._width = null;
		this._height = null;
		this._alt = null;
		this._onclick = null;
	}
}
