package com.isoft.iradar.tags;

public class CSubmit extends CButton {
	
	private static final long serialVersionUID = 1L;

	public CSubmit() {
		this("submit");
	}

	public CSubmit(String name) {
		this(name, "");
	}

	public CSubmit(String name, String caption) {
		this(name, caption, null);
	}

	public CSubmit(String name, String caption, String action) {
		this(name, caption, action, null);
	}

	public CSubmit(String name, String caption, String action, String styleclass) {
		super(name, caption, action, styleclass);
		this.setAttribute("type", "submit");
	}
}
