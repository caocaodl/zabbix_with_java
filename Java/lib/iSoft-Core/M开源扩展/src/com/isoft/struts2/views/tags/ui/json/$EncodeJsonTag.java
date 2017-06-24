package com.isoft.struts2.views.tags.ui.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $EncodeJsonTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $EncodeJsonText(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$EncodeJsonText aui = ($EncodeJsonText) component;
		if (jsVarName != null) {
			aui.setJsVarName(jsVarName);
		}
	}

	@Override
	public void release() {
		super.release();
		this.jsVarName = null;
	}

	private String jsVarName = null;

	public void setJsVarName(String jsVarName) {
		this.jsVarName = jsVarName;
	}

}
