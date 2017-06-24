package com.isoft.struts2.views.tags.ui.input;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputSecret extends AndurilUIComponent {
	
    private static final int DEFAULT_MAXLENGTH = Integer.MIN_VALUE;
	private String style = null;
    private String styleClass = null;
    private Integer maxlength = null;
    private String onblur = null;
    private String onchange = null;
    
    public $InputSecret(ValueStack stack, HttpServletRequest request,
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
		if (id != null && id.length() > 0) {
			writer.writeAttribute(HTML.ID_ATTR, id);
		}
		writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_PASSWORD);
		ValueBinding vb = getValueBinding("value");
		Object value = getValue();
		if (vb != null) {
			writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, value));
		} else {
			if (id != null && id.length() > 0) {
				writer.writeAttribute(HTML.NAME_ATTR, id);
			}
		}
		
		String _onchange = getOnchange();
		if(_onchange != null){
			writer.writeAttribute(HTML.ONCHANGE_ATTR, getOnchange());
		}
		String _onblur = getOnblur();
		if(_onblur != null){
			writer.writeAttribute(HTML.ONBLUR_ATTR, getOnblur());
		}
    	Integer _maxlength = getMaxlength();
    	if(_maxlength != null) {
    		writer.writeAttribute(HTML.MAXLENGTH_ATTR, getMaxlength());
    	}
    	String _style = getStyle();
		if(_style != null){
			writer.writeAttribute(HTML.STYLE_ATTR, getStyle());
		}
    	String _styleClass = getStyleClass();
		if(_styleClass != null){
			writer.writeAttribute(HTML.CLASS_ATTR, getStyleClass());
		}
		
		writer.endElement(HTML.INPUT_ELEM);
		return false;
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

	public Integer getMaxlength() {
		if(maxlength != null) {
			return maxlength.intValue();
		}else {
			return DEFAULT_MAXLENGTH;
		}
	}

	public void setMaxlength(Integer maxlength) {
		this.maxlength = maxlength;
	}

	public String getOnblur() {
		return onblur;
	}

	public void setOnblur(String onblur) {
		this.onblur = onblur;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}
}
