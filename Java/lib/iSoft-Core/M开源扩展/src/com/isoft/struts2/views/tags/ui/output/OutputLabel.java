package com.isoft.struts2.views.tags.ui.output;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.el.HTMLEncoder;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class OutputLabel extends AndurilUIComponent {

	private String style = null;
	
    public OutputLabel(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return true;
    }
    
    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
    	writer.startElement(HTML.LABEL_ELEM);
		String _style = getStyle();
		if (_style != null) {
			writer.writeAttribute(HTML.STYLE_ATTR, getStyle());
		}
		Object value = getValue();
		if (value != null) {
			writer.write(HTMLEncoder.encode(encodeValueAttr(value), true, true));
		}
		writer.endElement(HTML.LABEL_ELEM);
		return false;
    }

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
