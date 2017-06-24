package com.isoft.struts2.views.tags.jquery.easyui.layout;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class TabPage extends AndurilUIComponent {

	public TabPage(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.write("<div title=\"" + title + "\"");
		if (style != null && style.length() > 0) {
			writer.write(" style=\"" + style + "\"");
		} else {
			writer.write(" style=\"padding:10px\"");
		}
		writer.write(">");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("</div>");
		return false;
	}

	private String title;
	private String style;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
