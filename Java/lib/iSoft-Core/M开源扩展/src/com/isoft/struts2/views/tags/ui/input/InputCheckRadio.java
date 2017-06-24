package com.isoft.struts2.views.tags.ui.input;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class InputCheckRadio extends AndurilUIComponent{

	public InputCheckRadio(ValueStack stack, HttpServletRequest request,
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
        writer.writeAttribute(HTML.ID_ATTR, id);
       
        writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_RADIO);
        
        ValueBinding vb = getValueBinding("value");
        Object value =  getValue();
        
        if (vb != null) {
            writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, value));
        }else{
        	if(this._name != null && this._name.length()>0){
	       		 writer.writeAttribute(HTML.NAME_ATTR,this._name);
	       	}
        }
        
        if (value != null) {
            writer.writeAttribute(HTML.VALUE_ATTR, encodeValueAttr(value));
        }
        
        if(isChecked()){
            writer.writeAttribute(HTML.CHECKED_ATTR, HTML.CHECKED_ATTR);
        }
        
        if (isDisabled()) {
            writer.writeAttribute(HTML.DISABLED_ATTR, Boolean.TRUE);
        }
        
        if(this._onclick != null && this._onclick.length()>0){
        	writer.writeAttribute(HTML.ONCLICK_ATTR, this._onclick);
        }
        
        if(this._styleClass != null && this._styleClass.length()>0){
        	writer.writeAttribute(HTML.CLASS_ATTR, this._styleClass);
        }
        
        writer.endElement(HTML.INPUT_ELEM);
        return false;
    }
    private String _name;
    private String _onclick;
    private String _styleClass;
    private boolean _checked;
    private boolean _disabled;
    
    public void setName(String name) {
		this._name = name;
	}
	public String getName() {
		return _name;
	}
    
    public String getOnclick() {
		return _onclick;
	}
	public void setOnclick(String onclick) {
		this._onclick = onclick;
	}
	
	public String getStyleClass() {
		return _styleClass;
	}
	public void setStyleClass(String styleClass) {
		_styleClass = styleClass;
	}
	
	public boolean isChecked() {
		return _checked;
	}
	public void setChecked(boolean checked) {
		this._checked = checked;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public void setDisabled(boolean disabled) {
		this._disabled = disabled;
	}
}
