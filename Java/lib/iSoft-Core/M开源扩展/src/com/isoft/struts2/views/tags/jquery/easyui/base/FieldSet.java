package com.isoft.struts2.views.tags.jquery.easyui.base;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class FieldSet extends AndurilUIComponent {

	public FieldSet(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("<fieldset class='collapsible' ");
		if (style != null && style.length() > 0) {
			writer.writeLine("style='"+style+"'");
		}
		writer.writeLine(">");
		writer.writeLine("<legend>"+legend+"</legend>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("</fieldset>");
		return false;
	}

	private String style;
	private String legend;

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

}
