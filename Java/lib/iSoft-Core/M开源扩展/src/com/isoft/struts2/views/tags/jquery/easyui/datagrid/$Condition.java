package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $Condition extends AndurilComponent {

	public $Condition(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("<fieldset class='collapsible'>");
		writer.writeLine("<legend>查询条件</legend>");
		writer.writeLine("<div id='"+this.bindGridId+"Filter' style='white-space:nowrap'>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("</div>");
		writer.writeLine("</fieldset>");
		return false;
	}

	private String bindGridId;

	public String getBindGridId() {
		return bindGridId;
	}

	public void setBindGridId(String bindGridId) {
		this.bindGridId = bindGridId;
	}

}
