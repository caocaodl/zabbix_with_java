package com.isoft.struts2.views.tags.ui.html;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;
@Deprecated
public class $TabPage extends AndurilUIComponent {

	public $TabPage(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}
	
	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.write("<div title='"+this.title+"' "+(this.closable?"data-options='closable:true'":"")+">");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("</div>");
		return false;
	}

	private String title;
	private boolean closable;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}

}
