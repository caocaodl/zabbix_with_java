package com.isoft.struts2.views.tags.ui.input;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class InputTextButton extends AndurilUIComponent {

	public InputTextButton(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private String _onButtonClick;
	private String _onkeydown;
	private String _style;
	private String _value;

	public String getOnButtonClick() {
		return _onButtonClick;
	}

	public void setOnButtonClick(String buttonClick) {
		_onButtonClick = buttonClick;
	}

	public String getOnkeydown() {
		return _onkeydown;
	}

	public void setOnkeydown(String _onkeydown) {
		this._onkeydown = _onkeydown;
	}

	public String getStyle() {
		return _style;
	}

	public void setStyle(String _style) {
		this._style = _style;
	}

	@Override
    public String getValue() {
		return _value;
	}

	public void setValue(String _value) {
		this._value = _value;
	}

	@Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		createInputText(writer);
		createButton(writer);
		return false;
	}

	public void createInputText(HtmlResponseWriter writer) throws IOException {
		writer.startElement(HTML.INPUT_ELEM);
		writer.writeAttribute(HTML.ID_ATTR, getId());
		writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_TEXT);
		ValueBinding vb = getValueBinding("value");
		Object value = getValue();
		if (vb != null) {
			writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, value));
		} else {
			writer.writeAttribute(HTML.NAME_ATTR, getId());
		}
		if (value != null) {
			writer.writeAttribute(HTML.VALUE_ATTR, encodeValueAttr(value));
		}
		if (getOnkeydown() != null && getOnkeydown().length() != 0) {
			writer.writeAttribute(HTML.ONKEYDOWN_ATTR, getOnkeydown());
		}
		if (getStyle() != null && getStyle().length() != 0) {
			writer.writeAttribute(HTML.STYLE_ATTR, getStyle());
		}
		writer.endElement(HTML.INPUT_ELEM);
	}

	public void createButton(HtmlResponseWriter writer) throws IOException {
		writer.startElement(HTML.INPUT_ELEM);
		writer.writeAttribute(HTML.ID_ATTR, getId() + ":bt");
		writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_BUTTON);
		writer.writeAttribute(HTML.SRC_ATTR,  "/images/bt/search.gif");
		writer.writeAttribute(HTML.DATASRC_ATTR, getId() + ":bt");
		writer
				.writeAttribute(
						HTML.STYLE_ELEM,
						"width:16px;height:16px;background:url("
								+ "/images/icon/Icon.gif) no-repeat 1px -385px; border:none;cursor:hand;");
		if (getOnButtonClick() != null && getOnButtonClick().length() != 0) {
			writer.writeAttribute(HTML.ONCLICK_ATTR, getOnButtonClick());
		}
		writer.endElement(HTML.INPUT_ELEM);
	}
}
