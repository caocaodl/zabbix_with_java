package com.isoft.iradar.tags;

public class COptGroup extends CTag {
	
	private static final long serialVersionUID = 1L;

	public COptGroup(String label) {
		super("optgroup", "yes");
		this.attributes.put("label", label);
	}

}
