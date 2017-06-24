package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.FuncsUtil.formatDomId;

public class CFieldset extends CTag {

	private static final long serialVersionUID = 1L;

	public CFieldset() {
		this(null);
	}

	public CFieldset(Object items) {
		this(items, null);
	}

	public CFieldset(Object items, String styleclass) {
		this(items, styleclass, null);
	}

	public CFieldset(Object items, String styleclass, String id) {
		super("fieldset", "yes");
		this.attr("class", styleclass);
		if (!empty(id)) {
			this.attr("id", formatDomId(id));
		}
		this.addItem(items);

		this.tag_body_start = "";
		this.tag_start = "";
		this.tag_end = "";
		this.tag_body_end = "";
	}
}
