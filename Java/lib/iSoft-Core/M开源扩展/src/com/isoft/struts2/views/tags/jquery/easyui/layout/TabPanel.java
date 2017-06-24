package com.isoft.struts2.views.tags.jquery.easyui.layout;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class TabPanel extends AndurilUIComponent {

	public TabPanel(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.write("<div id='" + getId() + "' class=\"easyui-tabs\"");
		if (style != null && style.length() > 0) {
			writer.write(" style=\"" + style + "\"");
		}
		//writer.write(" data-options=\"tabPosition:'left',tabHeight:60,border:true\"");
		writer.write(">");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("</div>");
		writer.writeLine("<input type='hidden' id='tabs_"+getId()+"_selected' name='tabs_"+getId()+"_selected' value=''/>");
		writer.writeLine("<script type='text/javascript'>");
		writer.writeLine("$(document).ready(function() {");
		writer.writeLine("	$('#"+getId()+"').tabs({");
		writer.writeLine("		onSelect:function(title,index){");
		writer.writeLine("			$('#tabs_"+getId()+"_selected').val(index);");
		writer.writeLine("		}");
		writer.writeLine("	});");
		String selected = request.getParameter("tabs_"+getId()+"_selected");
		if(selected!=null && selected.length()>0){
			writer.writeLine("	$('#"+getId()+"').tabs('select',"+selected+");");
		}
		writer.writeLine("});");
		writer.writeLine("</script>");
		writer.writeLine("");
		writer.writeLine("");
		writer.writeLine("");		
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
