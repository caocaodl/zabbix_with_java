package com.isoft.struts2.views.tags.ui.input;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputButton extends AndurilUIComponent {

	public $InputButton(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private String _onclick;
	private Boolean _disabled;
	private String style;
	private String styleClass;

	public Boolean getDisabled() {
		return _disabled;
	}

	public void setDisabled(Boolean _disabled) {
		this._disabled = _disabled;
	}

	public String getOnclick() {
		return _onclick;
	}

	public void setOnclick(String _onclick) {
		this._onclick = _onclick;
	}

	public Boolean isDisabled() {
		return getDisabled() == null ? false : getDisabled();
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.startElement(HTML.ANCHOR_ELEM);
		writer.writeAttribute(HTML.ID_ATTR, getId());
		Object value = getValue();
		writer.writeAttribute(HTML.CLASS_ATTR, "button"+(isDisabled()?" disable":""));
		if (getOnclick() != null && getOnclick().length() != 0) {
			writer.writeAttribute(HTML.ONCLICK_ATTR, getOnclick());
		}
		String _style = this.getStyle();
    	if(_style != null) {
    		writer.writeAttribute(HTML.STYLE_ATTR, _style);
    	}
    	
    	String _styleClass = this.getStyleClass();
    	if(_styleClass != null) {
    		writer.writeAttribute(HTML.CLASS_ATTR, _styleClass);
    	}
		writer.write("<span class=\"cl\"></span>");
		writer.write("<span class=\"cr\"></span>");
		if(value!=null){
			writer.write(value.toString());
		}
		writer.endElement(HTML.ANCHOR_ELEM);
		return false;
	}

}
