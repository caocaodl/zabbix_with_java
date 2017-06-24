package com.isoft.iradar.tags;

import static com.isoft.iradar.inc.FuncsUtil.formatDomId;

import com.isoft.iradar.Cphp;

public class CLegend extends CTag {

	private static final long serialVersionUID = 1L;

	public CLegend() {
		this(null);
	}

	public CLegend(String id) {
		super("legend", "yes");
		if (!Cphp.is_null(id)) {
			this.attr("id", formatDomId(id));
		}
	}
}
