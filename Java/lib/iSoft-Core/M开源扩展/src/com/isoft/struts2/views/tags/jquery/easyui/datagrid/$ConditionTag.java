package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilTagSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class $ConditionTag extends AndurilTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $Condition(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$Condition aui = ($Condition) component;
		aui.setBindGridId(bindGridId);
	}

	@Override
	public void release() {
		this.bindGridId = null;
	}

	private String bindGridId;

	public void setBindGridId(String bindGridId) {
		this.bindGridId = bindGridId;
	}

}
