package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.ui.input.$InputButtonTag;
import com.opensymphony.xwork2.util.ValueStack;

public class $SearchButtonTag extends $InputButtonTag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $SearchButton(stack, req, res);
	}

	private String bindGridId;

	public void setBindGridId(String bindGridId) {
		this.bindGridId = bindGridId;
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$SearchButton tagBean = ($SearchButton) component;
		tagBean.setBindGridId(bindGridId);
	}

	@Override
	public void release() {
		super.release();
		this.bindGridId = null;
	}
}
