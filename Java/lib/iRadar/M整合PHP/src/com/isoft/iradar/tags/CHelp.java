package com.isoft.iradar.tags;

public class CHelp extends CIcon {

	private static final long serialVersionUID = 1L;

	public CHelp() {
		super("http://www.i-soft.com.cn/iradar/documentation", "iconhelp");
		this.onClick("window.open(\"http://www.i-soft.com.cn/iradar/documentation/\");");
	}

	public CHelp(String title) {
		this();
	}

	public CHelp(String title, String styleclass) {
		this();
	}
	
	public CHelp(String title, String styleclass, String action) {
		this();
	}
	
}
