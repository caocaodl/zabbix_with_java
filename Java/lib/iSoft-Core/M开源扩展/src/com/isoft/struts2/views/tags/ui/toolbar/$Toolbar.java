package com.isoft.struts2.views.tags.ui.toolbar;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.isoft.util.StringUtil;
import com.opensymphony.xwork2.util.ValueStack;

public class $Toolbar extends AndurilUIComponent {

	public $Toolbar(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	
	
	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		String style = this.getStyle();
		style = !StringUtil.isEmpty(style)? "": "style='"+style+"'";
		
		writer.write("<div id='"+this.getId()+"' "+style+" class='toolbar-ctn'>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
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
