package com.isoft.struts2.views.tags.ui.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $Body extends AndurilUIComponent {

	private String onload;
	private String onunload;
	private String oncontextmenu;
	private String style;

	public String getOnload() {
		return onload;
	}

	public void setOnload(String onload) {
		this.onload = onload;
	}

	public String getOnunload() {
		return onunload;
	}

	public void setOnunload(String onunload) {
		this.onunload = onunload;
	}

	public String getOncontextmenu() {
		return oncontextmenu;
	}

	public void setOncontextmenu(String oncontextmenu) {
		this.oncontextmenu = oncontextmenu;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public $Body(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.startElement("body");
		if (this.getId() != null && this.getId().length() > 0) {
			writer.writeAttribute(HTML.ID_ATTR, this.getId());
		}
		if (this.onload != null && this.onload.length() > 0) {
			writer.writeAttribute("onload", this.onload);
		}
		if (this.onunload != null && this.onunload.length() > 0) {
			writer.writeAttribute("onunload", this.onunload);
		}
		if (this.oncontextmenu != null && this.oncontextmenu.length() > 0) {
			writer.writeAttribute("oncontextmenu", this.oncontextmenu);
		}
		//writer.write("<div class=\"conLayer\">");

		writer.write("<div class='section'>");

		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		//writer.write("</div>");
		writer.write("</div>");
		writer.endElement("body");
		return false;
	}

}
