package com.isoft.struts2.views.tags.ui.input;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.util.HtmlRendererUtils;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputTextArea extends $InputText {

	public $InputTextArea(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private String _style;
	private String _styleClass;
	private Boolean _disabled;
	private Boolean _displayValueOnly;
	private String _onblur;
	private String _onchange;
	private String _onfocus;
	private String _onkeydown;
	private String _onkeyup;
	private String _rows;
	private String _cols;
	private String _name;

	@Override
    public String getOnblur() {
		return _onblur;
	}

	@Override
    public void setOnblur(String _onblur) {
		this._onblur = _onblur;
	}

	public Boolean getDisplayValueOnly() {
		return _displayValueOnly;
	}

	@Override
    public void setDisplayValueOnly(Boolean valueOnly) {
		_displayValueOnly = valueOnly;
	}
	public Boolean checkDisPlayValueOnlye(){
		return getDisplayValueOnly() == null?false:getDisplayValueOnly();
	}
	@Override
    public String getStyleClass() {
		return _styleClass;
	}

	@Override
    public void setStyleClass(String _styleClass) {
		this._styleClass = _styleClass;
	}

	@Override
    public String getStyle() {
		return _style;
	}

	@Override
    public void setStyle(String _style) {
		this._style = _style;
	}

	public Boolean getDisabled() {
		return _disabled;
	}

	public void setDisabled(Boolean _disabled) {
		this._disabled = _disabled;
	}

	public Boolean checkDisabled() {
		return _disabled == null ? false : _disabled;
	}


	@Override
    public String getOnchange() {
		return _onchange;
	}

	@Override
    public void setOnchange(String _onchange) {
		this._onchange = _onchange;
	}

	@Override
    public String getOnfocus() {
		return _onfocus;
	}

	@Override
    public void setOnfocus(String _onfocus) {
		this._onfocus = _onfocus;
	}

	@Override
    public String getOnkeydown() {
		return _onkeydown;
	}

	@Override
    public void setOnkeydown(String _onkeydown) {
		this._onkeydown = _onkeydown;
	}

	@Override
    public String getOnkeyup() {
		return _onkeyup;
	}

	@Override
    public void setOnkeyup(String _onkeyup) {
		this._onkeyup = _onkeyup;
	}

	public String getRows() {
		return _rows;
	}

	public void setRows(String _rows) {
		this._rows = _rows;
	}

	public String getCols() {
		return _cols;
	}

	public void setCols(String _cols) {
		this._cols = _cols;
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		if (checkDisPlayValueOnlye()) {
			HtmlRendererUtils.renderDisplayValueOnly(writer, this);
		} else {
			normalInputtextAear(writer);
		}
		return false;
	}

	public void normalInputtextAear(HtmlResponseWriter writer) throws IOException {
		writer.startElement(HTML.TEXTAREA_ELEM);
		writer.writeAttribute(HTML.ID_ATTR, getId());
		ValueBinding vb = getValueBinding("value");
		Object value = getValue();
		if (vb != null) {
			writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, value));
		} else {
			if (this._name != null && this._name.length() > 0) {
				writer.writeAttribute(HTML.NAME_ATTR, this._name);
			}
		}
		if (checkDisabled()) {
			writer.writeAttribute(HTML.DISABLED_ATTR, true);
		}
		if (getStyle() != null && getStyle().length() != 0) {
			writer.writeAttribute(HTML.STYLE_ELEM, getStyle());
		}
		if (getStyleClass() != null && getStyleClass().length() != 0) {
			writer.writeAttribute(HTML.CLASS_ATTR, getStyleClass());
		}
		if (getOnblur() != null && getOnblur().length() != 0) {
			writer.writeAttribute(HTML.ONBLUR_ATTR, this.getOnblur());
		}
		if (getOnchange() != null && getOnchange().length() != 0) {
			writer.writeAttribute(HTML.ONCHANGE_ATTR, this.getOnchange());
		}
		if (getOnfocus() != null && getOnfocus().length() != 0) {
			writer.writeAttribute(HTML.ONFOCUS_ATTR, this.getOnfocus());
		}
		if (getOnkeydown() != null && getOnkeydown().length() != 0) {
			writer.writeAttribute(HTML.ONKEYDOWN_ATTR, this.getOnkeydown());
		}
		if (getOnkeyup() != null && getOnkeyup().length() != 0) {
			writer.writeAttribute(HTML.ONKEYUP_ATTR, this.getOnkeyup());
		}
		if (getRows() != null) {
			writer.writeAttribute(HTML.ROWS_ATTR, this.getRows());
		}
		if (getCols() != null) {
			writer.writeAttribute(HTML.COLS_ATTR, this.getCols());
		}
		if (value != null) {
			writer.writeText(encodeValueAttr(value));
		}
		writer.endElement(HTML.TEXTAREA_ELEM);
	}

}
