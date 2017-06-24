package com.isoft.struts2.views.tags.ui.linkresources;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $LinkJs extends AndurilUIComponent {

	public $LinkJs(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private String _src;

	public String getSrc() {
		return _src;
	}

	public void setSrc(String _src) {
		this._src = _src;
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		linkJavaScript(writer, getSrc());
		return false;
	}
}
