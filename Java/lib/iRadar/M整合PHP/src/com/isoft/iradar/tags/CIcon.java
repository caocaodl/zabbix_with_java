package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.SPACE;

public class CIcon extends CSpan {
	
	private static final long serialVersionUID = 1L;

	public CIcon(String title, String styleclass) {
		this(title, styleclass, "");
	}

	public CIcon(String title, String styleclass, String action) {
		super(SPACE, styleclass+ " menu_icon shadow");
		this.attr("title", title);
		if (!empty(action)) {
			this.attr("onclick", "javascript:"+action);
		}
	}

}
