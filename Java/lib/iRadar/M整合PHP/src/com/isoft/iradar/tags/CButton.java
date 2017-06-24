package com.isoft.iradar.tags;

public class CButton extends CInput {
	
	private static final long serialVersionUID = 1L;
	
	public CButton() {
		this("button");
	}
	
	public CButton(String name) {
		this(name, "");
	}
	
	public CButton(String name, String caption) {
		this(name, caption, null);
	}
	
	public CButton(String name, String caption, String action) {
		this(name, caption, action, null);
	}

	public CButton(String name, String caption, String action, String styleclass) {
		super("button", name, caption, styleclass);
		this.attrEncStrategy = ENC_NOAMP;
		this.addAction("onclick", action);
	}

}
