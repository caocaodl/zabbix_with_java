package com.isoft.struts2.views.tags.ui.input;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputHidden extends AndurilUIComponent{
	
	private String styleClass = null;

	public $InputHidden(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}
	
    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return false;
    }

    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
    	
         String id = getId();
         writer.startElement(HTML.INPUT_ELEM);
         
         writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN);
         writer.writeAttribute(HTML.ID_ATTR, id);
         
         ValueBinding vb = getValueBinding("value");
         Object value =  getValue();
         
         if (vb != null) {
             writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, value));
         }else{
        	 if(id != null && id.length()>0){
        		 writer.writeAttribute(HTML.NAME_ATTR,id);
        	 }
         }
         
         if (value != null){
             writer.writeAttribute(HTML.VALUE_ATTR, encodeValueAttr(value));
         }
         
         if(styleClass != null && styleClass.length()>0) {
     		writer.writeAttribute(HTML.CLASS_ATTR, styleClass);
     	}

         writer.endElement(HTML.INPUT_ELEM);
         return false;
    }

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
    
    
}
