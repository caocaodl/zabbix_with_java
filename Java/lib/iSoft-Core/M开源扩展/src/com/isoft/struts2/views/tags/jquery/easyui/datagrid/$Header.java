package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $Header extends AndurilUIComponent {

	public $Header(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return true;
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
				model.setFrozen(frozen);
				(($DataGrid)component).getModel().addHeaderItem(model);
			}
			this.model = null;
		}
	}
	
	private HeaderItem model = new HeaderItem();
	protected void addColumnItem(ColumnItem item){
		model.addColumnItem(item);
	}

	private Boolean frozen;
	public Boolean getFrozen() {
		return frozen;
	}

	public void setFrozen(Boolean frozen) {
		this.frozen = frozen;
	}
	
}
