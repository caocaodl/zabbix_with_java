package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;

public class CList extends CTag {
	
	private static final long serialVersionUID = 1L;

	private boolean emptyList = true;

	public CList() {
		this(null);
	}
	public CList(Object items) {
		this(items, null);
	}
	public CList(Object items, String styleclass) {
		this(items, styleclass, null);
	}
	public CList(Object items, String styleclass, String emptyString) {
		super("ul", "yes");
		this.tag_end = "";
		this.addItem(items);
		this.addClass(styleclass);
		
		if(Cphp.is_null(items)){
			emptyString = (!Cphp.empty(emptyString)) ? emptyString : Cphp._("List is empty");
			this.addItem(emptyString, "empty");
			this.emptyList = true;
		}
	}

	public Object prepareItem(Object value, String styleclass, String id) {
		if (!Cphp.is_null(value)) {
			return new CListItem(value, styleclass, id);
		}
		return value;
	}
	
	@Override
	public CObject addItem(Object value) {
		this.addItem(value, null);
		return this;
	}
	public void addItem(Object value, String styleclass) {
		this.addItem(value, styleclass, null);
	}

	public void addItem(Object value, String styleclass, String id) {
		if (!Cphp.is_null(value) && this.emptyList) {
			this.emptyList = false;
			this.items.clear();
		}

		if (value instanceof CListItem) {
			super.addItem(value);
		} else {
			super.addItem(this.prepareItem(value, styleclass, id));
		}
	}
}
