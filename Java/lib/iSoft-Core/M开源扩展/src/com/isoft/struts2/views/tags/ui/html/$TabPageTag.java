package com.isoft.struts2.views.tags.ui.html;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;
@Deprecated
public class $TabPageTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $TabPage(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$TabPage aui = ($TabPage) component;
		aui.setTitle(title);
		aui.setClosable(getPropertyBooleanValue(closable));
	}

	@Override
	public void release() {
		super.release();
		this.title = null;
		this.closable = null;
	}

	private String title;
	private String closable;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setClosable(String closable) {
		this.closable = closable;
	}
}
