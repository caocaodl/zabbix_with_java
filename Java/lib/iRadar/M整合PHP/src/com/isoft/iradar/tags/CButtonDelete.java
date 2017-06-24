package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;

public class CButtonDelete extends CButtonQMessage {
	
	private static final long serialVersionUID = 1L;

	public CButtonDelete() {
		this(null);
	}

	public CButtonDelete(String msg) {
		this(msg, null);
	}

	public CButtonDelete(String msg, String vars) {
		this(msg, vars, null);
	}

	public CButtonDelete(String msg, String vars, String styleclass) {
		super("delete", Cphp._("Delete"), msg, vars, styleclass);
	}
}
