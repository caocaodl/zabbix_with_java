package com.isoft.struts2.views.tags.ui.block;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $Block extends AndurilUIComponent {

	public $Block(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.write("<div class='block'");
		if(StringUtils.isNotEmpty(this.style)){
			writer.write(" style='"+this.style+"'");
		} else {
//			writer.write(" style='float:left;");
		}
		writer.write(">");
		writer.write("<div class='block-detail'>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("</div>");
		writer.write("</div>");
		return false;
	}

	private String style;

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
