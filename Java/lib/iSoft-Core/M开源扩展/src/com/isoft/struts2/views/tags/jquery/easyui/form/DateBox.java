package com.isoft.struts2.views.tags.jquery.easyui.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class DateBox extends AndurilUIComponent {

	public DateBox(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("<input id='" + getId() + "' type='text'></input>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("<script type='text/javascript'>");
		writer.writeLine("$(document).ready(function(){");
		writer.writeLine("	$('#" + getId() + "').datebox({");
		if (panelWidth != null) {
			writer.writeLine("		panelWidth:" + panelWidth + ",");
		}
		if (panelHeight != null) {
			writer.writeLine("		panelHeight:" + panelHeight + ",");
		}
		if (currentText != null && currentText.length() > 0) {
			writer.writeLine("		currentText:'" + currentText + "',");
		}
		if (closeText != null && closeText.length() > 0) {
			writer.writeLine("		closeText:'" + closeText + "',");
		}
		if (okText != null && okText.length() > 0) {
			writer.writeLine("		okText:'" + okText + "',");
		}
		if (disabled != null) {
			writer.writeLine("		disabled:'true',");
		}
		if (this.getValue() != null) {
			writer.writeLine("		value:'" + getValue() + "',");
		}
		if (formatter != null && formatter.length() > 0) {
			writer.writeLine("		formatter:" + formatter + ",");
		}
		if (parser != null && parser.length() > 0) {
			writer.writeLine("		parser:" + parser + ",");
		}
		writer.writeLine("		author:'isoft'");
		writer.writeLine("	});");
		
		writer.writeLine("});");
		writer.writeLine("</script>");
		return false;
	}

	private Integer panelWidth;
	private Integer panelHeight;
	private String currentText;
	private String closeText;
	private String okText;
	private Boolean disabled;
	private String formatter;
	private String parser;

	public Integer getPanelWidth() {
		return panelWidth;
	}

	public void setPanelWidth(Integer panelWidth) {
		this.panelWidth = panelWidth;
	}

	public Integer getPanelHeight() {
		return panelHeight;
	}

	public void setPanelHeight(Integer panelHeight) {
		this.panelHeight = panelHeight;
	}

	public String getCurrentText() {
		return currentText;
	}

	public void setCurrentText(String currentText) {
		this.currentText = currentText;
	}

	public String getCloseText() {
		return closeText;
	}

	public void setCloseText(String closeText) {
		this.closeText = closeText;
	}

	public String getOkText() {
		return okText;
	}

	public void setOkText(String okText) {
		this.okText = okText;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	public String getParser() {
		return parser;
	}

	public void setParser(String parser) {
		this.parser = parser;
	}

}
