package com.isoft.struts2.views.tags.ui.breadcrumb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilTagSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class $BreadCrumbItemTag extends AndurilTagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $UIBreadCrumbItem(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$UIBreadCrumbItem aui = ($UIBreadCrumbItem) component;
		if (_itemLabel != null) {
			if (isValueReference(_itemLabel)) {
				this.setValueBinding(aui, "itemLabel", _itemLabel);
			} else {
				aui.setItemLabel(_itemLabel);
			}
		}
	}

	@Override
	public void release() {
		this._itemLabel = null;
	}

	private String _itemLabel;

	public void setItemLabel(String itemLabel) {
		this._itemLabel = itemLabel;
	}
}
