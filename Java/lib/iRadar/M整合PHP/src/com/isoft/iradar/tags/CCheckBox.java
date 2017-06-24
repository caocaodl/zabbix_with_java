package com.isoft.iradar.tags;

public class CCheckBox extends CInput {
	
	private static final long serialVersionUID = 1L;

	public CCheckBox() {
		this("checkbox");
	}

	public CCheckBox(String name) {
		this(name, false);
	}

	public CCheckBox(String name, boolean checked) {
		this(name, checked, null);
	}

	public CCheckBox(String name, boolean checked, String action) {
		this(name, checked, action, "yes");
	}

	public CCheckBox(String name, boolean checked, String action, int value) {
		this(name, checked, action, String.valueOf(value));
	}
	
	public CCheckBox(String name, boolean checked, String action, String value) {
		super("checkbox", name, value, "checkbox pointer");
		this.setAttribute("onclick", action);
		this.setChecked(checked);
	}

	public CCheckBox setChecked(boolean checked) {
		if (checked) {
			this.attributes.put("checked", "checked");
		} else {
			this.removeAttribute("checked");
		}
		return this;
	}
}
