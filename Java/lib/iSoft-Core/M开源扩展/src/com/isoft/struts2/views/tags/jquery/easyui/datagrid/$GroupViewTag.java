package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $GroupViewTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $GroupView(stack, req, res);
	}

	@Override
	public void release() {
		super.release();
		this.field = null;
		this.formatter = null;
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$GroupView groupView = ($GroupView) component;
		GroupViewItem model = new GroupViewItem();
		model.setField(this.field);
		model.setFormatter(this.formatter);
		groupView.setModel(model);
	}

	private String field;
	private String formatter;

	public void setField(String field) {
		this.field = field;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

}
