package com.isoft.iradar.tags;

public class CListItem extends CTag {
	
	private static final long serialVersionUID = 1L;
	
	public CListItem(Object items) {
		this(items, null);
	}
	
	public CListItem(Object items, String styleclass) {
		this(items, styleclass, null);
	}

	public CListItem(Object items, String styleclass, String id) {
		super("li", "yes");
		this.attr("id", id);
		this.addClass(styleclass);
		this.addItem(items);
	}
	
}
