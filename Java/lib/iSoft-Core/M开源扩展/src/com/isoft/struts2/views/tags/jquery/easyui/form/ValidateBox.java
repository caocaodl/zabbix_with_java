package com.isoft.struts2.views.tags.jquery.easyui.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class ValidateBox extends AndurilUIComponent {

	public ValidateBox(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("<input id='" + getId() + "'></input>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("<script type='text/javascript'>");
		writer.writeLine("$(document).ready(function(){");
		writer.writeLine("	$('#" + getId() + "')."+getFunc()+"({");
		renderValues(writer);
		renderProperties(writer);
		writer.writeLine("		author:'isoft'");
		writer.write("	});");
		
		renderStyleClass(writer);
		
		writer.writeLine("	$('#" + getId() + "').addClass('isoft-input-text');");
		writer.writeLine("});");
		writer.writeLine("</script>");
		return false;
	}

	private void renderStyleClass(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("	$('#" + getId() + "').addClass('isoft-input-text')");
		if (styleClass != null && styleClass.length() > 0) {
			writer.write(".addClass('"+styleClass+"')");
		}
		writer.writeLine(";");
	}

	protected String getFunc(){
		return "validatebox";
	}
	
	protected void renderProperties(HtmlResponseWriter writer)
			throws IOException {
		if (required != null) {
			writer.writeLine("		required:" + required + ",");
		}
		if (validType != null && validType.length() > 0) {
			writer.writeLine("		validType:[" + validType + "],");
		}
		if (delay != null) {
			writer.writeLine("		delay:" + delay + ",");
		}
		if (missingMessage != null && missingMessage.length() > 0) {
			writer.writeLine("		missingMessage:'" + missingMessage + "',");
		}
		if (invalidMessage != null && invalidMessage.length() > 0) {
			writer.writeLine("		invalidMessage:'" + invalidMessage + "',");
		}
		if (tipPosition != null && tipPosition.length() > 0) {
			writer.writeLine("		tipPosition:'" + tipPosition + "',");
		}
		if (deltaX != null) {
			writer.writeLine("		deltaX:" + deltaX + ",");
		}
		if (novalidate != null) {
			writer.writeLine("		novalidate:" + novalidate + ",");
		}
	}

	private void renderValues(HtmlResponseWriter writer) throws IOException {
		if (getValue() != null) {
			writer.writeLine("		value:'" + getValue() + "',");
		}
	}

	private String styleClass;
	private Boolean required;
	private String validType;
	private Integer delay;
	private String missingMessage;
	private String invalidMessage;
	private String tipPosition;
	private Integer deltaX;
	private Boolean novalidate;
	
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setValidType(String validType) {
		this.validType = validType;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	public void setMissingMessage(String missingMessage) {
		this.missingMessage = missingMessage;
	}

	public void setInvalidMessage(String invalidMessage) {
		this.invalidMessage = invalidMessage;
	}

	public void setTipPosition(String tipPosition) {
		this.tipPosition = tipPosition;
	}

	public void setDeltaX(Integer deltaX) {
		this.deltaX = deltaX;
	}

	public void setNovalidate(Boolean novalidate) {
		this.novalidate = novalidate;
	}
}
