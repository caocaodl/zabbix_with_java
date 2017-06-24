package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.inc.FuncsUtil;

public class CLabel extends CTag {
	
	private static final long serialVersionUID = 1L;
	
	public CLabel(Object label) {
		this(label, null);
	}

	public CLabel(Object label, String forAttr) {
		this(label, forAttr, null);
	}
	
	public CLabel(Object label, String forAttr, String id) {
		super("label", "yes", label);
		if (!Cphp.is_null(id)) {
			this.attr("id", FuncsUtil.formatDomId(id));
		}
		if (!Cphp.is_null(forAttr)) {
			this.attr("for", FuncsUtil.formatDomId(forAttr));
		}
	}
}
