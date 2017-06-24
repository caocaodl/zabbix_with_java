package com.isoft.iradar.tags;

public class CParam extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CParam(String name, Object value) {
		super("param", "no");
		this.attributes.put("name", name);
		this.attributes.put("value", value);
	}

}
