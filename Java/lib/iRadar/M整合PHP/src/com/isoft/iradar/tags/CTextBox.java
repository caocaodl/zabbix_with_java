package com.isoft.iradar.tags;

import com.isoft.iradar.inc.Defines;

public class CTextBox extends CInput {
	
	private static final long serialVersionUID = 1L;

	//private String caption;
	
	public CTextBox() {
		this("textbox");
	}
	
	public CTextBox(String name) {
		this(name, "");
	}
	
	public CTextBox(String name, String value) {
		this(name, value, 20);
	}
	
	public CTextBox(String name, String value, Integer size) {
		this(name, value, size, false);
	}
	
	public CTextBox(String name, String value, Integer size, boolean readonly) {
		this(name, value, size, readonly, 255);
	}
	
	public CTextBox(String name, String value, Integer size, boolean readonly,
			int maxlength) {
		super("text", name, value);
		this.setReadonly(readonly);
		//this.caption = null;
		this.tag_body_start = "";
		this.setAttribute("size", size);
		this.setAttribute("maxlength", maxlength);
		
		// require for align input field using css width
		if (size != null && size == Defines.RDA_TEXTBOX_STANDARD_SIZE) {
			this.setAttribute("style", "width: " + Defines.RDA_TEXTAREA_STANDARD_WIDTH + "px;");
		}
	}

}
