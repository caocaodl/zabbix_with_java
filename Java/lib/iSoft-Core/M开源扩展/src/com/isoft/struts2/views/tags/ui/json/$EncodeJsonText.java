package com.isoft.struts2.views.tags.ui.json;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.json.JsonUtil;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $EncodeJsonText extends AndurilUIComponent {

	private String jsVarName;

	public $EncodeJsonText(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		ValueBinding vb = getValueBinding("value");
		
		if(this.jsVarName!=null && this.jsVarName.trim().length()>0) {
			writer.writeLine("<script type='text/javascript'>");
			writer.write("var ");
			writer.write(this.jsVarName);
			writer.write("=");
			Object value = getValue();
			if (vb != null) {
				writer.write(JsonUtil.encodeObject2Json(value));
			} else {
				writer.write("null");
			}
			writer.write(";");
			writer.writeLine("</script>");
		}else {
			if (vb != null) {
				Object value = getValue();
				writer.write(JsonUtil.encodeObject2Json(value));
			}else {
				writer.write("{}");
			}
		}
		return false;
	}

	public String getJsVarName() {
		return jsVarName;
	}

	public void setJsVarName(String jsVarName) {
		this.jsVarName = jsVarName;
	}
}
