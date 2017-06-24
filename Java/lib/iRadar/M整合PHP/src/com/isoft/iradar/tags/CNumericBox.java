package com.isoft.iradar.tags;

public class CNumericBox extends CInput {
	
	private static final long serialVersionUID = 1L;

	public CNumericBox() {
		this("number");
	}

	public CNumericBox(String name) {
		this(name, "0");
	}

	public CNumericBox(String name, String value) {
		this(name, value, 20);
	}

	public CNumericBox(String name, String value, int size) {
		this(name, value, size, false);
	}

	public CNumericBox(String name, String value, int size, boolean readonly) {
		this(name, value, size, readonly, false);
	}

	public CNumericBox(String name, String value, int size, boolean readonly,
			boolean allowempty) {
		this(name, value, size, readonly, allowempty, true);
	}

	public CNumericBox(String name, String value, int size, boolean readonly,
			boolean allowempty, boolean allownegative) {
		super("text", name, value);
		this.setReadonly(readonly);
		this.attr("size", size);
		this.attr("maxlength", size);
		this.attr("style", "text-align: right;");
		this.addAction("onchange", "validateNumericBox(this, "
				+ (allowempty ? "true" : "false") + ", "
				+ (allownegative ? "true" : "false") + ");");
	}

}
