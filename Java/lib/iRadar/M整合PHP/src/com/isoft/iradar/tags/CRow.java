package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;
import com.isoft.types.CArray;

public class CRow extends CTag {
	
	private static final long serialVersionUID = 1L;
	
	public CRow() {
		this(null,null,null);
	}
	
	public CRow(Object item) {
		this(item,null,null);
	}
	
	public CRow(Object item, String styleclass) {
		this(item,styleclass,null);
	}

	public CRow(Object item, String styleclass, String id) {
		super("tr", "yes");
		this.addItem(item);
		this.attr("class", styleclass);
		this.attr("id", id);
	}

	public void setAlign(Object value) {
		this.attr("align", String.valueOf(value));
	}

	@Override
	public CObject addItem(Object item) {
		if (item != null) {
			if (item instanceof CCol) {
				super.addItem(item);
			} else if (Cphp.isArray(item)) {
				for (Object el: CArray.valueOf(item)) {
					if (el != null) {
						if (el instanceof CCol) {
							super.addItem(el);
						} else {
							super.addItem(new CCol(el));
						}
					}
				}
			} else {
				super.addItem(new CCol(item));
			}
		}
		return this;
	}

	public void setWidth(Object value) {
		if (value instanceof String) {
			this.attr("width", value);
		}
	}

}
