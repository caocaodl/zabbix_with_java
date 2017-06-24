package com.isoft.struts2.views.tags.ui.output;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class Pagination extends AndurilUIComponent {

	public Pagination(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
//        Object value = getValue();
//        if (value != null) {
//            writer.write(HTMLEncoder.encode(value.toString(), true, true));
//        }
		String baseUrl = getContextPath();
		writer.write("<div class=\"table-footer\">\n");
		writer.write("<div class=\"table-data-summary\">第 20 - 40 条纪录 / 共 230 条纪录</div>\n");
		writer.write("<div class=\"table-pagination\">\n");
		writer.write("第<input type=text value=\"8\">页 / 共 12 页\n");
		writer.write("<a href=\"#\" class=\"pagination-button\"><img src=\""+baseUrl+"/assets/images/pagination-first-arrow.png\" /></a>\n");
		writer.write("<a href=\"#\" class=\"pagination-button\"><img src=\""+baseUrl+"/assets/images/pagination-prev-arrow.png\" /></a>\n");
		writer.write("<a href=\"#\" class=\"pagination-button disable\"><img src=\""+baseUrl+"/assets/images/pagination-next-arrow.png\" /></a>\n");
		writer.write("<a href=\"#\" class=\"pagination-button disable\"><img src=\""+baseUrl+"/assets/images/pagination-last-arrow.png\" /></a>\n");
		writer.write("</div>\n");
		writer.write("</div>\n");
		return false;
	}
}
