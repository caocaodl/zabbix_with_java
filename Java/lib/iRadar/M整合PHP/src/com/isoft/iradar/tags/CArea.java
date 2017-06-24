package com.isoft.iradar.tags;

import org.apache.commons.lang3.StringUtils;

public class CArea extends CTag {

	private static final long serialVersionUID = 1L;

	public CArea(int[] coords, String href, String alt, String shape) {
		super("area", "no");
		this.setCoords(coords);
		this.setShape(shape);
		this.setHref(href);
		this.setAlt(alt);
	}

	public void setCoords(int[] value) {
		if (value.length < 0) {
			throw new RuntimeException("Incorrect values count for setCoords " + value.length + ".");
		}
		String s = StringUtils.join(value, ',');
		this.setAttribute("coords", s);
	}
	
	public void setShape(String value) {
		this.setAttribute("shape", value);
	}
	
	public void setHref(String value) {
		this.setAttribute("href", value);
	}
	
	public void setAlt(String value) {
		this.setAttribute("alt", value);
	}
}
