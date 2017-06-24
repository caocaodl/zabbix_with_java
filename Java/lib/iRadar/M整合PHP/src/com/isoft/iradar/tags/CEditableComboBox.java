package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.inc.JsUtil;

public class CEditableComboBox extends CComboBox {
	
	private static final long serialVersionUID = 1L;
	
	public CEditableComboBox() {
		this("editablecombobox");
	}
	
	public CEditableComboBox(String name) {
		this(name, null);
	}
	
	public CEditableComboBox(String name, String value) {
		this(name, value, 0);
	}
	
	public CEditableComboBox(String name, String value, int size) {
		this(name, value, size, null);
	}

	public CEditableComboBox(String name, String value, int size, String action) {
		super(name,  value, action);
		JsUtil.insert_javascript_for_editable_combobox();
		this.addAction("onfocus", "CEditableComboBoxInit(this);");
		this.addAction("onchange", "CEditableComboBoxOnChange(this, "+ size +");");
	}
	
	public void addItem(Object value, String caption) {
		this.addItem(value, caption, null);
	}
	
	public void addItem(Object value, String caption, Boolean selected) {
		this.addItem(value, caption, selected, true);
	}
	
	public void addItem(Object value, String caption, Boolean selected,
			boolean enabled) {
		this.addItem(value, caption, selected, enabled, null);
	}
	
	@Override
	public void addItem(Object value, String caption, Boolean selected,
			boolean enabled, String styleclass) {
		if (Cphp.is_null(selected)) {
			if (Cphp.isArray(this.value)) {
				if (Cphp.inArray(value, (Object[]) this.value)) {
					this.value_exist = true;
				}
			} else if (value.equals(this.value)) {
				this.value_exist = true;
			}
		}
		super.addItem(value, caption, selected, enabled, styleclass);
	}

	@Override
	public String toString(Boolean destroy) {
		if (!Cphp.isset(this.value_exist) && !Cphp.empty(this.value)) {
			this.addItem(this.value, this.value.toString(), true);
		}
		return super.toString(destroy);
	}

}
