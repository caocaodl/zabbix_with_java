package com.isoft.struts2.views.tags.ui.graphic;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class GraphicImage extends AndurilUIComponent {

	public GraphicImage(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private String _border;
	private String _style;
	private String _styleClass;
	private String _title;
	private String _width;
	private String _height;
	private String _alt;
	private String _onclick;

	public String getBorder() {
		return _border;
	}

	public void setBorder(String _border) {
		this._border = _border;
	}
	
	public String getStyle() {
		return _style;
	}

	public void setStyle(String _style) {
		this._style = _style;
	}

	public String getStyleClass() {
		return _styleClass;
	}

	public void setStyleClass(String class1) {
		_styleClass = class1;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String _title) {
		this._title = _title;
	}
	
	public String getWidth() {
		return _width;
	}

	public void setWidth(String width) {
		this._width = width;
	}

	public String getHeight() {
		return _height;
	}

	public void setHeight(String height) {
		this._height = height;
	}

	public String getAlt() {
		return _alt;
	}

	public void setAlt(String alt) {
		this._alt = alt;
	}
	
	public String getOnclick() {
		return _onclick;
	}

	public void setOnclick(String _onclick) {
		this._onclick = _onclick;
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.startElement(HTML.IMG_ELEM);
		writer.writeAttribute(HTML.ID_ATTR, getId());
		String src = (String)getValue();		
		String baseUrl = getContextPath();
		StringBuilder res = new StringBuilder(baseUrl);
		if (!baseUrl.endsWith("/")) {
			res.append('/');
		}
		if (src.startsWith("/")) {
			res.append(src.substring(1));
		} else {
			res.append(src);
		}
		writer.writeAttribute(HTML.SRC_ATTR, res);
		if (_width != null && _width.length() != 0) {
			writer.writeAttribute(HTML.WIDTH_ATTR, _width);
		}
		if (_height != null && _height.length() != 0) {
			writer.writeAttribute(HTML.HEIGHT_ATTR, _height);
		}
		if (_alt != null && _alt.length() != 0) {
			writer.writeAttribute(HTML.ALT_ATTR, _alt);
		}
		if (getStyle() != null && getStyle().length() != 0) {
			writer.writeAttribute(HTML.STYLE_ATTR, getStyle());
		}
		if (getStyleClass() != null && getStyleClass().length() != 0) {
			writer.writeAttribute(HTML.CLASS_ATTR, getStyleClass());
		}
		if (getTitle() != null && getTitle().length() != 0) {
			writer.writeAttribute(HTML.TITLE_ATTR, getTitle());
		}
		if (getBorder() != null && getBorder().length() != 0) {
			writer.writeAttribute(HTML.BORDER_ATTR, getBorder());
		} else {
			writer.writeAttribute(HTML.BORDER_ATTR, 0);
		}
		if (getOnclick() != null && getOnclick().length() != 0) {
			writer.writeAttribute(HTML.ONCLICK_ATTR, getOnclick());
		}
		writer.endElement(HTML.IMG_ELEM);
		return false;
	}
}
