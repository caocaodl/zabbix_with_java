package com.isoft.struts2.views.tags.ui.toolbar;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $ToolbarButton extends AndurilUIComponent {

	public $ToolbarButton(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private final static String HTML = "<a href='#|' " +
			"class='easyui-linkbutton' " +
			"data-options='iconCls:\"%s\", disabled:%s' " +
			"onclick='javascript:%s'" +
		">%s</a>";
	
	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		String click = (this.onclick==null||this.onclick.trim().length()==0)? "void(0)": this.onclick;
		String html = String.format(HTML, icon, disabled, click, name);
		writer.write(html);
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	private String name;
	private String icon;
	private String onclick;
	private boolean disabled;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
