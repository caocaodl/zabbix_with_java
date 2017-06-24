package com.isoft.struts2.views.tags.ui.input;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class FlagHidden extends AndurilUIComponent {

    public FlagHidden(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return true;
    }
    
    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
        Object value = getValue();
        String id = getId();
        writer.startElement(HTML.INPUT_ELEM);
        if(id != null && id.length() > 0){
        	writer.writeAttribute(HTML.ID_ATTR, id);
        }
        writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN);
        writer.writeAttribute(HTML.VALUE_ATTR, value != null ? value.toString() : "");
        writer.endElement(HTML.INPUT_ELEM);
        return false;
    }

}
