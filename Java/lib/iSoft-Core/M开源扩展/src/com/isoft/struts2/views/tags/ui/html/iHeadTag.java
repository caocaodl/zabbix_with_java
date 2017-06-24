package com.isoft.struts2.views.tags.ui.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class iHeadTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;


	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new iHead(stack, req, res);
	}

}
