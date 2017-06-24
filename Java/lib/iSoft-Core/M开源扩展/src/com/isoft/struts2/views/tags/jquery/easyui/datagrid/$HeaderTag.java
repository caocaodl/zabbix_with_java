package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $HeaderTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $Header(stack, req, res);
	}

	@Override
	public void release() {
		super.release();
		this.frozen = null;
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$Header header = ($Header)component;
		header.setFrozen(getPropertyBooleanValue(frozen,null));
	}

	private String frozen;

	public void setFrozen(String frozen) {
		this.frozen = frozen;
	}

}
