package com.isoft.struts2.views.tags.ui.linkresources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $LinkJsTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $LinkJs(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$LinkJs tagBean = ($LinkJs) component;
		tagBean.setSrc(_src);
	}

	@Override
	public void release() {
		super.release();
		this._src = null;
	}

	private String _src;

	public void setSrc(String _src) {
		this._src = _src;
	}
}
