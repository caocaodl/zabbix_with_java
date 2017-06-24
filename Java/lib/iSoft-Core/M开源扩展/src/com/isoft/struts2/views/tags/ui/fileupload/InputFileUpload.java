package com.isoft.struts2.views.tags.ui.fileupload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class InputFileUpload extends AndurilUIComponent {
	
	private static final boolean DEFAULT_CONTENT_EDITABLE = true;
	private static final int DEFAULT_SIZE = Integer.MIN_VALUE;
	
    private String style = null;
    private String onchange = null;
    private String onkeydown = null;
    private Integer size = null;
    private Boolean contentEditable = false;
    private String onselectstart = null;
    
    public InputFileUpload(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
        this.setConverter("uf");
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
		writer.writeAttribute(HTML.TYPE_ATTR, "file");
        
		ValueBinding vb = getValueBinding("value");
        if (vb != null) {
            writer.writeAttribute(HTML.NAME_ATTR, encodeNameAttr(vb, null));
        }else if (id != null && id.length() > 0) {
			writer.writeAttribute(HTML.NAME_ATTR, id);
		}

		String _onchange = getOnchange();
		if (_onchange != null) {
			writer.writeAttribute(HTML.ONCHANGE_ATTR, getOnchange());
		}

		String _style = getStyle();
		if (_style != null) {
			writer.writeAttribute(HTML.STYLE_ATTR, getStyle());
		}

		String _onkeydown = getOnkeydown();
		if (_onkeydown != null) {
			writer.writeAttribute(HTML.ONKEYDOWN_ATTR, getOnkeydown());
		}

		boolean isContentEditable = getContentEditable();
		if(!isContentEditable) {
			writer.writeAttribute(InputFileUploadTag.CONTENT_EDITABLE_ATTR, String.valueOf(getContentEditable()));
		}

		Integer _size = getSize();
		if (_size != null) {
			writer.writeAttribute(HTML.SIZE_ATTR, getSize());
		}

		String _onselectstart = getOnselectstart();
		if (_onselectstart != null) {
			writer.writeAttribute(InputFileUploadTag.ONSELECTSTART_ATTR, getOnselectstart());
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

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public String getOnkeydown() {
		return onkeydown;
	}

	public void setOnkeydown(String onkeydown) {
		this.onkeydown = onkeydown;
	}

	public boolean getContentEditable() {
		if(contentEditable != null){
			return contentEditable.booleanValue();
		}else{
			return DEFAULT_CONTENT_EDITABLE;
		}
	}

	public void setContentEditable(Boolean contentEditable) {
		this.contentEditable = contentEditable;
	}

	public String getOnselectstart() {
		return onselectstart;
	}

	public void setOnselectstart(String onselectstart) {
		this.onselectstart = onselectstart;
	}

	public int getSize() {
		if(size != null){
			return size.intValue();
		}else{
			return DEFAULT_SIZE;
		}
	}

	public void setSize(Integer size) {
		this.size = size;
	}
}
