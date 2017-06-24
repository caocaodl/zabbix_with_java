package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.types.CArray.array;

import com.isoft.types.CArray;

public class CWarning extends CTable {
	
	private static final long serialVersionUID = 1L;
	
	protected String header;
	protected CArray message;
	protected String alignment;
	protected String paddings;
	protected CArray buttons;
	
	public CWarning(String header) {
		this(header, null);
	}

	public CWarning(String header, CArray message) {
		super(null, "warningTable");
		setAlign("center");
		this.header = header;
		this.message = message;
		this.alignment = null;
		this.paddings = null;
		this.buttons = array();
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public void setPaddings(String padding) {
		this.paddings = padding;
	}

	public void setButtons(Object buttons) {
		this.buttons = buttons instanceof CArray ? (CArray)buttons : array(buttons);
	}
	
	@Override
	public void show() {
		this.show(true);
	}

	@Override
	public void show(Boolean destroy) {
		setHeader(header, "header");

		String cssClass = "content";
		if (!empty(alignment)) {
			cssClass += " "+alignment;
		}

		if (!empty(paddings)) {
			addRow(paddings);
			addRow(new CSpan(message), cssClass);
			addRow(paddings);
		} else {
			addRow(new CSpan(message), cssClass);
		}

		setFooter(new CDiv(buttons, "buttons"), "footer");
		super.show(destroy);
	}
	
	
}
