package com.isoft.struts2.views.tags.jquery.easyui.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class FieldSetTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new FieldSet(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		FieldSet tagBean = (FieldSet) component;
		tagBean.setStyle(style);
		tagBean.setLegend(legend);
	}

	@Override
	public void release() {
		super.release();
		this.style = null;
		this.legend = null;
	}

	private String style;
	private String legend;

	public void setStyle(String style) {
		this.style = style;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

}
