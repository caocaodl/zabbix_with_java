package com.isoft.iradar.tags;

import com.isoft.iradar.inc.HtmlUtil;

public class CComboItem extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CComboItem(Object value) {
		this(value, null);
	}
	
	public CComboItem(Object value, String caption) {
		this(value, caption, false);
	}
	
	public CComboItem(Object value, String caption, boolean selected) {
		this(value, caption, selected, null);
	}

	public CComboItem(Object value, String caption, boolean selected, Boolean enabled) {
		super("option", "yes");
		this.tag_body_start = "";
		this.setAttribute("value", value);
		this.addItem(caption);
		this.setSelected(selected);
		this.setEnabled(enabled);
	}

	public void setValue(Object value) {
		this.attributes.put("value", value);
	}

	public Object getValue() {
		return this.getAttribute("value");
	}

	public void setCaption(String value) {
		this.addItem(HtmlUtil.nbsp(value));
	}

	public void setSelected(boolean selected) {
		if (selected) {
			this.attributes.put("selected", "selected");
		} else {
			this.removeAttribute("selected");
		}
	}
}
