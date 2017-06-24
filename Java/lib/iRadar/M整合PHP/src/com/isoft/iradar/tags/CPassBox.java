package com.isoft.iradar.tags;

public class CPassBox extends CInput {
	
	private static final long serialVersionUID = 1L;

	public CPassBox() {
		this("password");
	}

	public CPassBox(String name) {
		this(name, "");
	}

	public CPassBox(String name, String value) {
		this(name, value, 50);
	}

	public CPassBox(String name, String value, int size) {
		this(name, value, size, 255);
	}

	public CPassBox(String name, String value, int size, int maxlength) {
		super("password", name, value);
		this.setAttribute("size", size);
		this.setAttribute("maxlength", maxlength);
	}

}
