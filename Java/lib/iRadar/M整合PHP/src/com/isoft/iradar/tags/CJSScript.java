package com.isoft.iradar.tags;

public class CJSScript extends CObject {
	
	private static final long serialVersionUID = 1L;
	
	public CJSScript() {
		this(null);
	}

	public CJSScript(Object item) {
		this.addItem(item);
	}

	@Override
	public CObject addItem(Object value) {
		return super.addItem(value);
//		if (is_array(_value)) {
////			foreach (_value as _item) {
//				array_push(this.items, unpack_object(_item));
////			}
//		}
//		else if (!is_null(_value)) {
//			this.items.add(unpack_object(_value).toString());
//		}
//		return this;
	}

}
