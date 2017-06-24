package com.isoft.struts2.views.tags.ui.select;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class SelectBooleanCheckbox extends AndurilUIComponent{

	public SelectBooleanCheckbox(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return false;
    }

    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
    	
    	if(isDisabled()){
    		renderDisabledCheckBox(writer);
    	}
    	renderCheckbox(writer,null);
    	
    	return false;
    }
    
    protected void renderDisabledCheckBox(HtmlResponseWriter writer)throws IOException{
    	 writer.startElement(HTML.INPUT_ELEM);
         writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN);
         writer.writeAttribute(HTML.ID_ATTR, getId());
         Object value = getValue();
         boolean isChecked = false ;
         if(value!=null){
         	isChecked = value instanceof Boolean? (Boolean) value:Boolean.valueOf((String)value);
         }
         if(isChecked){
             writer.writeAttribute(HTML.VALUE_ATTR, value);
         }
         writer.endElement(HTML.INPUT_ELEM);
    }
    
    protected void renderCheckbox(HtmlResponseWriter writer,String label) throws IOException {

    	writer.startElement(HTML.INPUT_ELEM);
        writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_CHECKBOX);
        writer.writeAttribute(HTML.ID_ATTR, getId());
        
        ValueBinding vb = getValueBinding("value");
        Object value = getValue();
        boolean isChecked = false ;
        if (vb != null) {
            writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, value));
        }
        if(value!=null){
        	isChecked = value instanceof Boolean? (Boolean) value:Boolean.valueOf((String)value);
        }
        
        writer.writeAttribute(HTML.VALUE_ATTR, "true");
        
        if (isChecked) {
            writer.writeAttribute(HTML.CHECKED_ATTR, HTML.CHECKED_ATTR);
        }
        
        if (isDisabled()) {
            writer.writeAttribute(HTML.DISABLED_ATTR, HTML.DISABLED_ATTR);
        }
        
        if(this._onclick != null && this._onclick.length()>0){
        	writer.writeAttribute(HTML.ONCLICK_ATTR, this._onclick);
        }

        if (isDisabled()) {
            writer.writeAttribute(HTML.DISABLED_ATTR, Boolean.TRUE);
        }
        
        if ((label != null) && (label.length() > 0)) {
            writer.write(HTML.NBSP_ENTITY);
            writer.writeText(label);
        }
        writer.endElement(HTML.INPUT_ELEM);
    }
    
    private boolean _disabled;
    private String _onclick;
	public boolean isDisabled() {
		return _disabled;
	}

	public void setDisabled(boolean disabled) {
		this._disabled = disabled;
	}

	public String getOnclick() {
		return _onclick;
	}

	public void setOnclick(String onclick) {
		this._onclick = onclick;
	}
}
