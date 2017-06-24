package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;

public class CRadioButton extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CRadioButton(String name, Object value) {
		this(name, value, null);
	}
	
	public CRadioButton(String name, Object value, String styleclass) {
		this(name, value, styleclass, null);
	}
	
	public CRadioButton(String name, Object value, String styleclass,
			String id) {
		this(name, value, styleclass, id, false);
	}
	
	public CRadioButton(String name, Object value, String styleclass,
			String id, boolean checked) {
		this(name, value, styleclass, id, checked, null);
	}

	public CRadioButton(String name, Object value, String styleclass,
			String id, boolean checked, String action) {
		super("input", "no");
		this.setAttribute("class", "radio " + styleclass);
		this.setAttribute("name", name);
		this.setAttribute("value", value);
		this.setAttribute("id", id);
		this.setAttribute("type", "radio");
		if (checked) {
			this.setAttribute("checked", "checked");
		}
		if (!Cphp.empty(action)) {
			this.setAttribute("onchange", action);
		}
	}

}
