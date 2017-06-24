package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;

public class CCol extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CCol() {
		this(null, null, null, (String) null);
	}

	public CCol(Object item) {
		this(item, null, null, (String) null);
	}

	public CCol(Object item, Object styleclass) {
		this(item, styleclass, null, (String) null);
	}

	public CCol(Object item, Object styleclass, String colspan) {
		this(item, styleclass, colspan, (String) null);
	}

	public CCol(Object item, Object styleclass, Integer colspan) {
		this(item, styleclass, colspan.toString(), (String) null);
	}

	public CCol(Object item, Object styleclass, String colspan, Integer width) {
		this(item, styleclass, colspan, String.valueOf(width));
	}

	public CCol(Object item, Object styleclass, String colspan, String width) {
		super("td", "yes");
		this.addItem(item);
		this.attr("class", styleclass);
		if (!Cphp.empty(colspan)) {
			this.attr("colspan", colspan);
		}
		if (!Cphp.empty(width)) {
			this.attr("width", width);
		}
	}

	public void setAlign(Object value) {
		this.attr("align", String.valueOf(value));
	}

	public void setRowSpan(Object value) {
		this.attr("rowspan", String.valueOf(value));
	}

	public void setColSpan(Object value) {
		this.attr("colspan", String.valueOf(value));
	}

	public void setWidth(Object value) {
		if (value instanceof String) {
			this.attr("width", value);
		}
	}
}
