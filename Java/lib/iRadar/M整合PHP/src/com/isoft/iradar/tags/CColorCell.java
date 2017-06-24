package com.isoft.iradar.tags;

import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;

public class CColorCell extends CDiv{
	
	private static final long serialVersionUID = 1L;

	public CColorCell(String name, String value) {
		this(name, value, null);
	}

	public CColorCell(String name, String value, String action) {
		super(SPACE+SPACE+SPACE, "pointer");
		setName(name);
		attr("id", rda_formatDomId(name));
		attr("title", "#"+value);
		attr("style", "display: inline; width: 10px; height: 10px; text-decoration: none; border: 1px solid black; background-color: #"+value);
		attr("onclick", action);
	}

}
