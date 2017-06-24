package com.isoft.iradar.tags;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.types.CArray;

public class CObject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public List<String> items = new ArrayList<String>();

	public CObject() {
		this(null);
	}

	public CObject(Object items) {
		if (items != null) {
			this.addItem(items);
		}
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(Boolean destroy) {
		if (destroy == null) {
			destroy = true;
		}
		String res = Cphp.implode("", items.toArray());
		if (destroy) {
			destroy();
		}
		return res;
	}

	public void show() {
		this.show(true);
	}

	public void show(Boolean destroy) {
		RadarContext ctx = RadarContext.getContext();
		try {
			PrintWriter writer = ctx.getResponse().getWriter();
			writer.write(this.toString(destroy));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void destroy() {
		//this.cleanItems();
	}

	public void cleanItems() {
		this.items.clear();
	}

	public int itemsCount() {
		return this.items.size();
	}

	protected CObject addItem(Object value) {
		return _addItem(value);
	}
	
	private CObject _addItem(Object value) {
		if (value != null) {
			if (Cphp.is_string(value)) {
				this.items.add((String) value);
			} else if (Cphp.isArray(value)) {
				for (Object data: CArray.valueOf(value)) {
					this._addItem(data);
				}
			}  else if (value instanceof List) {
				List datas = (List)value;
				for(Object data:datas){
					this._addItem(data);
				}
			}else {
				this.items.add(unpack_object(value).toString());
			}
		}
		return this;
	}

	public static StringBuilder unpack_object(Object item) {
		StringBuilder res = new StringBuilder();
		if (item != null) {
			if (item.getClass().isArray()) {
				int len = Array.getLength(item);
				for (int i = 0; i < len; i++) {
					res.append(unpack_object(Array.get(item, i)));
				}
			} else if (item instanceof CArray) {
				for (Object obj: (CArray)item) {
					res.append(unpack_object(obj));
				}
			} else {
				res.append(item);
			}
		}
		return res;
	}
}
