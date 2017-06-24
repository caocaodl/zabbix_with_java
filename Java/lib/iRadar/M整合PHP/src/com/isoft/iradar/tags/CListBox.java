package com.isoft.iradar.tags;

public class CListBox extends CComboBox {
	
	private static final long serialVersionUID = 1L;
	
	public CListBox() {
		this("listbox");
	}
	
	public CListBox(String name) {
		this(name, null);
	}
	
	public CListBox(String name, Object value) {
		this(name, value, 5);
	}
	
	public CListBox(String name, Object value, int size) {
		this(name, value, size, null);
	}

	public CListBox(String name, Object value, int size, String action) {
		super(name,  null, action);
		this.attributes.put("multiple", "multiple");
		this.attributes.put("size", size);
		this.setValue(value);
	}

	public void setSize(int value) {
		this.attr("size", value);
	}
}
