package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;

public class CAreaMap extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CAreaMap() {
		this("");
	}

	public CAreaMap(String name) {
		super("map", "yes");
		this.setName(name);
	}
	
	public void addRectArea(int x1, int y1, int x2, int y2, String href, String alt) {
		this.addArea(new int[] { x1, y1, x2, y2 }, href, alt, "rect");
	}

	public void addArea(int[] coords, String href, String alt, String shape) {
		this.addItem(new CArea(coords, href, alt, shape));
	}
	
	@Override
	public CObject addItem(Object value) {
		if (Cphp.is_object(value) && value instanceof CArea) {
			throw new RuntimeException("Incorrect value for addItem "+value);
		}
		return super.addItem(value);
	}
}
