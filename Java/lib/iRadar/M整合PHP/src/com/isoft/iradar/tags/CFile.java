package com.isoft.iradar.tags;

public class CFile extends CInput {
	
	private static final long serialVersionUID = 1L;

	public CFile() {
		this("file");
	}

	public CFile(String name) {
		this(name, "");
	}

	public CFile(String name, String value) {
		super("file", name, value);
		this.setFile(value);
	}

	public void setFile(String value) {
		this.setAttribute("value", value);
	}
}
