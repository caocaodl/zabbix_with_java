package com.isoft.struts2.views.tags.jquery.easyui.layout;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class CriteriaItem extends AndurilUIComponent {

	public CriteriaItem(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.write("<div style='width:400px'>");
		writer.write("<div style='float:left;line-height:2;width:160px'>");
		if (this.caption != null && this.caption.length() > 0) {
			writer.write(this.caption);
		}
		writer.write("</div>");
		writer.write("<div style='float:left;width:240px'>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("</div>");
		writer.write("</div>");	
		return false;
	}

	private String caption;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
}
