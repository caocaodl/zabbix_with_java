package com.isoft.struts2.views.tags.ui.html;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.server.RunParams;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class iHead extends AndurilUIComponent {

	public iHead(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		String baseUrl = getContextPath();
		writer.write("<head>\n");
		writer.write("<meta charset=\"utf-8\"/>\n");
		writer.write("<meta http-equiv='pragma' content='no-cache'>\n");
		writer.write("<meta http-equiv='cache-control' content='no-cache, must-revalidate'>\n");
		writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\">\n");
		writer.write("<title>"+RunParams.TITLE+"</title>\n");
		writer.write("<script type=\"text/javascript\">var ctxpath='"+baseUrl+"';</script>\n");
		linkJavaScript(writer, iHead.class);
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("</head>");
		return false;
	}
}
