package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $GroupView extends AndurilUIComponent {

	public $GroupView(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected void popComponentStack() {
		super.popComponentStack();
		if (this.isRendered()) {
			Object component = this.getComponentStack().peek();
			if (component instanceof $DataGrid) {
				(($DataGrid) component).getModel().setGroupView(this.model);
			}
			this.model = null;
		}
	}

	private GroupViewItem model;

	public void setModel(GroupViewItem model) {
		this.model = model;
	}

}
