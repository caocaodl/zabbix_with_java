package com.isoft.iradar.tags;

public class CSpan extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CSpan() {
		this(null, null, null);
	}

	public CSpan(Object items) {
		this(items, null, null);
	}

	public CSpan(Object items, String styleclass) {
		this(items, styleclass, null);
	}

	public CSpan(Object items, String styleclass, String id) {
		super("span", "yes");
		this.attr("class", styleclass);
		this.attr("id", id);
		this.addItem(items);
		this.tag_body_start = "";
		this.tag_start = "";
		this.tag_end = "";
		this.tag_body_end = "";
	}
}
