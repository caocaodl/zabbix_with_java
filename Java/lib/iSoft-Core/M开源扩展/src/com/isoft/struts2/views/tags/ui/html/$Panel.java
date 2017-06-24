package com.isoft.struts2.views.tags.ui.html;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.isoft.util.StringUtil;
import com.opensymphony.xwork2.util.ValueStack;

public class $Panel extends AndurilUIComponent {

	public $Panel(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		String style = "";
		String widthStyle = "";
		String clz = "";
		if(!StringUtil.isEmpty(this.style)){
			style = "style='"+this.style+"'";
		}
		if(!StringUtil.isEmpty(width)) {
			widthStyle = "style='width: "+this.width+"';";
		}
		if(!StringUtil.isEmpty(this.iconClass)) {
			clz += this.iconClass;
		}
		
		writer.writeLine("<div class='panel-ctn "+clz+"' "+widthStyle+" >" );
		writer.writeLine("<div class='easyui-panel' " +
				"id='"+this.getId()+"' " +
				"title='"+this.getTitle()+"' " +
				"icon='' " +
				"collapsible='"+this.collapsible+"' " +
				"minimizable='"+this.minimizable+"' " +
				"maximizable='"+this.maximizable+"' " +
				"closable='"+this.closable+"' " +
				""+style+">");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("</div>");
		writer.write("</div>");
		return false;
	}

	private String title;
	private String width;
	private String style;
	private String widgetstyle;
	private String iconClass;
	private boolean closable;
	private boolean collapsible;
	private boolean minimizable;
	private boolean maximizable;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getStyle() {
		return style;
	}

	public String getWidgetstyle() {
		return widgetstyle;
	}

	public void setWidgetstyle(String widgetstyle) {
		this.widgetstyle = widgetstyle;
	}

	public String getIconClass() {
		return iconClass;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

	public boolean isMinimizable() {
		return minimizable;
	}

	public void setMinimizable(boolean minimizable) {
		this.minimizable = minimizable;
	}

	public boolean isMaximizable() {
		return maximizable;
	}

	public void setMaximizable(boolean maximizable) {
		this.maximizable = maximizable;
	}

}
