package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.is_null;

public class CIFrame extends CTag {

	private static final long serialVersionUID = 1L;

	public CIFrame() {
		this(null);
	}

	public CIFrame(String src) {
		this(src, "100%");
	}

	public CIFrame(String src, String width) {
		this(src, width, "100%");
	}

	public CIFrame(String src, String width, String height) {
		this(src, width, height, "no");
	}

	public CIFrame(String src, String width, String height, String scrolling) {
		this(src, width, height, scrolling, "iframe");
	}

	public CIFrame(String src, String width, String height, String scrolling, String id) {
		super("iframe", "yes");
		this.tag_start = "";
		this.tag_end = "";
		this.tag_body_start = "";
		this.tag_body_end = "";

		this.setSrc(src);
		this.setWidth(width);
		this.setHeight(height);
		this.setScrolling(scrolling);
		this.setAttribute("id", id);
	}

	public void setSrc(String value) {
		if (is_null(value)) {
			this.removeAttribute("src");
		} else {
			this.setAttribute("src", value);
		}
	}

	public void setWidth(String value) {
		if (is_null(value)) {
			this.removeAttribute("width");
		} else {
			this.setAttribute("width", value);
		}
	}

	public void setHeight(String value) {
		if (is_null(value)) {
			this.removeAttribute("height");
		} else {
			this.setAttribute("height", value);
		}
	}

	public void setScrolling(String value) {
		if (is_null(value)) {
			value = "no";
		}
		this.setAttribute("scrolling", value);
	}
}
