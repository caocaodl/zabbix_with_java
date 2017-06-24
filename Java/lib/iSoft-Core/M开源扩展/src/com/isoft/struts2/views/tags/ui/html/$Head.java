package com.isoft.struts2.views.tags.ui.html;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.server.RunParams;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $Head extends AndurilUIComponent {

	public $Head(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		String baseUrl = getContextPath();
		writer.writeLine("<head>");
		writer.writeLine("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		writer.writeLine("<meta http-equiv='pragma' content='no-cache'>");
		writer.writeLine("<meta http-equiv='cache-control' content='no-cache, must-revalidate'>");
		writer.writeLine("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\">");
		if (this.title == null || this.title.length() == 0) {
			this.title = RunParams.TITLE;
		}
		writer.writeLine("<title>" + this.title + "</title>");

		linkCss(writer, $Head.class);
		writer.writeLine("<script type='text/javascript'>var ctxpath='" + baseUrl + "';</script>");
		linkJavaScript(writer, $Head.class);

		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("</head>");
		return false;
	}
}
