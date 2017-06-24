package com.isoft.struts2.views.tags.ui.html;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $Html extends AndurilUIComponent {

	public $Html(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		writer.write("<!--[if lt IE 7]><html class=\"ie ie6 ie67\" lang=\"zh\"><![endif]-->");
		writer.write("<!--[if IE 7]><html class=\"ie ie7 ie67\" lang=\"zh\"><![endif]-->");
		writer.write("<!--[if IE 8]><html class=\"ie ie8\" lang=\"zh\"><![endif]-->");
		writer.write("<!--[if IE 9]><html class=\"ie ie9\" lang=\"zh\"><![endif]-->");
		writer.write("<!--[if gt IE 9]><!--><html lang=\"zh\"><!--<![endif]-->");
		writer.write("\n");		
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("</html>");
		return false;
	}
}
