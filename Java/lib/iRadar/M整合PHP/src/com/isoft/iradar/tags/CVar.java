package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CVar {

	private List var_container = new ArrayList();
	private String var_name;
	private String element_id;
	
	public CVar(String name, Object value) {
		this(name, value, null);
	}
	
	public CVar(String name, Object value, String id) {
		this.var_name = name;
		this.element_id = id;
		this.setValue(value);
	}
	
	public void setValue(Object value) {
		if (!is_null(value)) {
			this.parseValue(this.var_name, value);
		}
	}

	public void parseValue(String name, Object value) {
		if (isArray(value)) {
			for (Entry<Object, Object> e : ((CArray<Object>)CArray.valueOf(value)).entrySet()) {
				String key = Nest.as(e.getKey()).asString();
				Object item = e.getValue();
				if (is_null(item)) {
					continue;
				}
				this.parseValue(name+"["+key+"]", item);
			}
			return;
		}

		String svalue = Nest.as(value).asString();
		if (svalue.indexOf('\n') == -1) {
			CInput hiddenVar = new CInput("hidden", name, svalue, null, this.element_id);
			hiddenVar.removeAttr("class");
			this.var_container.add(hiddenVar);
		} else {
			CTextArea hiddenVar = new CTextArea(name, svalue);
			hiddenVar.setAttribute("class", "hidden");
			this.var_container.add(hiddenVar);
		}
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (Object item : this.var_container) {
			res.append(item.toString());
		}
		return res.toString();
	}
	
}
