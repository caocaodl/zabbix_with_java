package com.isoft.struts2.views.tags.jquery.easyui.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class DateBoxTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new DateBox(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		DateBox aui = (DateBox) component;
		if (panelWidth != null && panelWidth.length() > 0) {
			aui.setPanelWidth(getPropertyIntegerValue(panelWidth));
		}
		if (panelHeight != null && panelHeight.length() > 0) {
			aui.setPanelHeight(getPropertyIntegerValue(panelHeight));
		}
		if (currentText != null && currentText.length() > 0) {
			aui.setCurrentText(getPropertyStringValue(currentText));
		}
		if (closeText != null && closeText.length() > 0) {
			aui.setCloseText(getPropertyStringValue(closeText));
		}
		if (okText != null && okText.length() > 0) {
			aui.setOkText(getPropertyStringValue(okText));
		}
		if (disabled != null && disabled.length() > 0) {
			aui.setDisabled(getPropertyBooleanValue(disabled));
		}
		if (formatter != null && formatter.length() > 0) {
			aui.setFormatter(getPropertyStringValue(formatter));
		}
		if (parser != null && parser.length() > 0) {
			aui.setParser(getPropertyStringValue(parser));
		}
	}

	@Override
	public void release() {
		super.release();
		this.panelWidth = null;
		this.panelHeight = null;
		this.currentText = null;
		this.closeText = null;
		this.okText = null;
		this.disabled = null;
		this.formatter = null;
		this.parser = null;
	}

	private String panelWidth;
	private String panelHeight;
	private String currentText;
	private String closeText;
	private String okText;
	private String disabled;
	private String formatter;
	private String parser;

	public void setPanelWidth(String panelWidth) {
		this.panelWidth = panelWidth;
	}

	public void setPanelHeight(String panelHeight) {
		this.panelHeight = panelHeight;
	}

	public void setCurrentText(String currentText) {
		this.currentText = currentText;
	}

	public void setCloseText(String closeText) {
		this.closeText = closeText;
	}

	public void setOkText(String okText) {
		this.okText = okText;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	public void setParser(String parser) {
		this.parser = parser;
	}

}
