package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.isoft.struts2.views.tags.ui.input.$InputButton;
import com.opensymphony.xwork2.util.ValueStack;

public class $SearchButton extends $InputButton {

	public $SearchButton(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private String bindGridId;

	public String getBindGridId() {
		return bindGridId;
	}

	public void setBindGridId(String bindGridId) {
		this.bindGridId = bindGridId;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		boolean ret = super.encodeEnd(writer);
	    
	    writer.writeLine("<script type='text/javascript'>");
	    writer.writeLine("$(document).ready(function(){");
	    writer.writeLine("	  $(\"#"+this.getId()+"\").click(function(){");
	    writer.writeLine("		  $('#"+this.bindGridId+"').datagrid('load',getSearchFilter('"+this.bindGridId+"'));");
	    writer.writeLine("	  });");
	    writer.writeLine("});");
	    writer.writeLine("</script>");
	    
		return ret;
	}

}
