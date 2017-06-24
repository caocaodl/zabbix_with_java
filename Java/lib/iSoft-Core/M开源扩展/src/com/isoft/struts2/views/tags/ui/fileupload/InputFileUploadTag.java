package com.isoft.struts2.views.tags.ui.fileupload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class InputFileUploadTag extends AndurilUITag {
	public static final String CONTENT_EDITABLE_ATTR = "contentEditable"; 
	public static final String ONSELECTSTART_ATTR = "onselectstart";	
    private static final long serialVersionUID = 1L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        return new InputFileUpload(stack, req, res);
    }

    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        InputFileUpload tagComp = (InputFileUpload) component;
        if (style != null) {
        	tagComp.setStyle(style);
		}
		if (onkeydown != null) {
			tagComp.setOnkeydown(onkeydown);
		}
		if (size != null) {
			tagComp.setSize(getPropertyIntegerValue(size));
		}
		if(onchange != null){
			tagComp.setOnchange(onchange);
		}
		if (contentEditable != null) {
			tagComp.setContentEditable(getPropertyBooleanValue(contentEditable));
		}
		if (onselectstart != null) {
			tagComp.setOnchange(onselectstart);
		}
    }
    
    @Override
    public void release() {
        super.release();
        this.style = null;
        this.onchange = null;
        this.onkeydown = null;
        this.size = null;
        this.contentEditable = null;
        this.onselectstart = null;
    }
    
    private String style = null;
    private String onchange = null;
    private String onkeydown = null;
    private String size = null;
    private String contentEditable = null;
    private String onselectstart = null;
    
    
    public void setStyle(String style) {
		this.style = style;
	}

	public void setOnkeydown(String onkeydown) {
		this.onkeydown = onkeydown;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setContentEditable(String contentEditable) {
		this.contentEditable = contentEditable;
	}

	public void setOnselectstart(String onselectstart) {
		this.onselectstart = onselectstart;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

}
