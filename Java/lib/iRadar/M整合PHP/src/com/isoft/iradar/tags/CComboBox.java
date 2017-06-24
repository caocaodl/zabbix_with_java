package com.isoft.iradar.tags;

import java.util.Map.Entry;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CComboBox extends CTag {

	private static final long serialVersionUID = 1L;
	
	protected Object value;
	protected Boolean value_exist;
	
	public CComboBox() {
		this("combobox");
	}
	
	public CComboBox(String name) {
		this(name, null);
	}
	
	public CComboBox(String name, Object value) {
		this(name, value, null);
	}
	
	public CComboBox(String name, Object value, String action) {
		this(name, value, action, null);
	}

	public CComboBox(String name, Object value, String action, CArray<String> items) {
		super("select", "yes");
		this.tag_end = "";
		this.attr("id", FuncsUtil.formatDomId(name));
		this.attr("name", name);
		this.attr("class", "input select");
		this.attr("size", 1);
		this.value = value;
		this.attr("onchange", action);
		this.addItems(items);
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public void addItems(CArray<String> items) {
		if (items != null) {
			for (Entry<Object, String> e : items.entrySet()) {
				Object value = e.getKey();
				String caption = Nest.as(e.getValue()).asString();
				boolean selected = Cphp.equals(value, this.value);
				super.addItem(new CComboItem(value, caption, selected));
			}
		}
	}
	
	public void addItemsInGroup(String label, CArray<String> items) {
		COptGroup group = new COptGroup(label);
		for (Entry<Object, String> e : items.entrySet()) {
			Object value = e.getKey();
			String caption = e.getValue();
			boolean selected = Cphp.equals(value, this.value);
			group.addItem(new CComboItem(value, caption, selected));
			
			if (Cphp.strcmp(EasyObject.asString(value), EasyObject.asString(this.value)) == 0) {
				this.value_exist = true;
			}
		}
		super.addItem(group);
	}
		
	public void addItem(Object value, String caption) {
		this.addItem(value, caption, null);
	}
	public void addItem(Object value, String caption, Boolean selected) {
		this.addItem(value, caption, selected, true);
	}
	public void addItem(Object value, String caption, Boolean selected, boolean enabled) {
		this.addItem(value, caption, selected, enabled, null);
	}
	public void addItem(Object value, String caption, Boolean selected, boolean enabled, String styleclass) {
		if (value instanceof CComboItem || value instanceof COptGroup) {
			super.addItem(value);
		} else {
			if (Cphp.is_null(selected)) {
				selected = false;
				if (Cphp.is_array(this.value)) {
					if (Cphp.in_array(value, CArray.valueOf(this.value))) {
						selected = true;
					}
				} else if (Cphp.equals(value, this.value)) {
					selected = true;
				}
			} else {
				selected = true;
			}

			CComboItem citem = new CComboItem(value, caption, selected, enabled);

			if (styleclass != null) {
				citem.addClass(styleclass);
			}

			super.addItem(citem);
		}
	}

}
