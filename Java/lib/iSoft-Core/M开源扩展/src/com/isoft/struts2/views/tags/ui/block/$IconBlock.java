package com.isoft.struts2.views.tags.ui.block;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $IconBlock extends AndurilUIComponent {

	public $IconBlock(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.write("<div class='area'");
		if(StringUtils.isNotEmpty(this.width)){
			writer.write(" style='width:"+this.width+"'");
		}
		writer.writeLine(">");
		writer.write("<div class='area-icon'>");
		writer.write("<img src='"+getContextPath()+this.icon+"'");
		if(StringUtils.isNotEmpty(this.iconStyle)){
			writer.write(" style='"+this.iconStyle+"'");
		}
		writer.write(">");
		writer.writeLine("</div>");
		writer.write("<div class='area-detail table-header'>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("</div>");
		writer.writeLine("</div>");
		return false;
	}

	private String icon;
	private String width;
	private String iconStyle;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getIconStyle() {
		return iconStyle;
	}

	public void setIconStyle(String iconStyle) {
		this.iconStyle = iconStyle;
	}

}
