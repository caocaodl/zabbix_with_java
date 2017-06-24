package com.isoft.struts2.views.tags.ui.input;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.util.HtmlRendererUtils;
import com.isoft.struts2.util.HtmlRendererUtils.DisplayValueOnlyCapable;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $InputText extends AndurilUIComponent implements DisplayValueOnlyCapable {
	
	private static final boolean DEFAULT_DISABLED = false;
    private static final boolean DEFAULT_READONLY = false;
	
	private String style = null;
    private String styleClass = null;
    
    //HTML 4.0 event-handler attributes
    private String onclick = null;
    private String onkeypress = null;
    private String onkeydown = null;
    private String onkeyup = null;
    
    //HTML 4.0 input attributes
    private String align = null;
    private Boolean disabled = false;
    private Integer maxlength = null;
    private String onblur = null;
    private String onchange = null;
    private String onfocus = null;
    private Boolean readonly = false;
    private Integer size = null;
    
	private Boolean displayValueOnly;
	
    public $InputText(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }
    
	@Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return false;
    }

    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
    	if(this.isDisplayValueOnly()) {
    		HtmlRendererUtils.renderDisplayValueOnly(writer, this);
    	}else {
    		this.renderNormal(writer);
    	}
        return false;
    }
    
    public void renderDisplayValueOnlyAttributes(HtmlResponseWriter writer) throws IOException {
    	this.renderUniversalAttribute(writer);
    	this.renderEventHandlerAttribute(writer);
    }
    
    protected void renderNormal(HtmlResponseWriter writer) throws IOException{
    	writer.startElement(HTML.INPUT_ELEM);
        writer.writeAttribute(HTML.ID_ATTR, this.getId());
        writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_TEXT);        
        
        ValueBinding vb = getValueBinding("value");
        Object value = getValue();
        if (vb != null) {
            writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, value));
        }else {
        	writer.writeAttribute(HTML.NAME_ATTR, this.getId());
        }
        
        if (value != null) {
            writer.writeURIAttribute(HTML.VALUE_ATTR, encodeValueAttr(value));
        }
        
        writer.writeAttribute(HTML.DISABLED_ATTR, this.isDisabled());
        this.renderGeneralAttribute(writer);
        
        writer.endElement(HTML.INPUT_ELEM);
    }
    
    protected void renderGeneralAttribute(HtmlResponseWriter writer) throws IOException {
    	this.renderUniversalAttribute(writer);
    	this.renderEventHandlerAttribute(writer);
    	this.renderInputAttribute(writer);
    }
    
    protected void renderUniversalAttribute(HtmlResponseWriter writer) throws IOException {
    	String _style = this.getStyle();
    	if(_style != null) {
    		writer.writeAttribute(HTML.STYLE_ATTR, _style);
    	}
    	
    	String _styleClass = this.getStyleClass();
    	if(_styleClass != null) {
    		writer.writeAttribute(HTML.CLASS_ATTR, _styleClass);
    	}
    }
    
    protected void renderEventHandlerAttribute(HtmlResponseWriter writer) throws IOException {
    	String _onclick = this.getOnclick();
    	if(_onclick != null) {
    		writer.writeAttribute(HTML.ONCLICK_ATTR, _onclick);
    	}
    	
    	String _onkeypress = this.getOnkeypress();
    	if(_onkeypress != null) {
    		writer.writeAttribute(HTML.ONKEYPRESS_ATTR, _onkeypress);
    	}
    	
    	String _onkeydown = this.getOnkeydown();
    	if(_onkeydown != null) {
    		writer.writeAttribute(HTML.ONKEYDOWN_ATTR, _onkeydown);
    	}
    	
    	String _onkeyup = this.getOnkeyup();
    	if(_onkeyup != null) {
    		writer.writeAttribute(HTML.ONKEYUP_ATTR, this.getOnkeyup());
    	}    	
    }
    
    protected void renderInputAttribute(HtmlResponseWriter writer) throws IOException {
    	String _align = this.getAlign();
    	if(_align != null) {
    		writer.writeAttribute(HTML.ALIGN_ATTR, _align);
    	}
    	
    	Boolean _readlonly = this.isReadonly();
    	if(_readlonly != null) {
    		writer.writeAttribute(HTML.READONLY_ATTR, _readlonly);
    	}
    	    	
    	String _onblur = this.getOnblur();
    	if(_onblur != null) {
    		writer.writeAttribute(HTML.ONBLUR_ATTR, _onblur);
    	}    	
    	String _onchange = this.getOnchange();
    	if(_onchange != null) {
    		writer.writeAttribute(HTML.ONCHANGE_ATTR, _onchange);
    	}
    	String _onfocus = this.getOnfocus();
    	if(_onfocus != null) {
    		writer.writeAttribute(HTML.ONFOCUS_ATTR, this.getOnfocus());
    	}
    	
    	Integer _maxlength = this.getMaxlength();
    	if(_maxlength != null) {
    		writer.writeAttribute(HTML.MAXLENGTH_ATTR, this.getMaxlength());
    	}
    	Integer _size = this.getSize();
    	if(_size != null) {
    		writer.writeAttribute(HTML.SIZE_ATTR, _size);
    	}    	
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

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}


	public String getOnkeypress() {
		return onkeypress;
	}

	public void setOnkeypress(String onkeypress) {
		this.onkeypress = onkeypress;
	}

	public String getOnkeydown() {
		return onkeydown;
	}

	public void setOnkeydown(String onkeydown) {
		this.onkeydown = onkeydown;
	}

	public String getOnkeyup() {
		return onkeyup;
	}

	public void setOnkeyup(String onkeyup) {
		this.onkeyup = onkeyup;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public boolean isDisabled() {
		if(disabled != null) {
			return disabled.booleanValue();
		}else {
			return DEFAULT_DISABLED;
		}
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}	
	
	public Integer getMaxlength() {
		return maxlength;
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

	public String getOnfocus() {
		return onfocus;
	}

	public void setOnfocus(String onfocus) {
		this.onfocus = onfocus;
	}

	public boolean isReadonly() {
		if(readonly != null) {
			return readonly.booleanValue();
		}else {
			return DEFAULT_READONLY;
		}
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}
	
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	public boolean isDisplayValueOnly() {
		if(displayValueOnly != null) {
			return displayValueOnly.booleanValue();
		}
		return false;
	}

	public void setDisplayValueOnly(Boolean displayValueOnly) {
		this.displayValueOnly = displayValueOnly;
	}
	
	
}
