package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;

public class CImg extends CTag {
	
	private static final long serialVersionUID = 1L;
	
	public CImg(String src) {
		this(src, null);
	}
	
	public CImg(String src, String name) {
		this(src, name, null);
	}
	
	public CImg(String src, String name, Integer width) {
		this(src, name, width, null);
	}
	
	public CImg(String src, String name, Integer width, Integer height) {
		this(src, name, width, height, null);
	}

	public CImg(String src, String name, Integer width, Integer height,
			String styleclass) {
		super("img", "no");
		this.tag_start = "";
		this.tag_end = "";
		this.tag_body_start = "";
		this.tag_body_end = "";

		if (Cphp.is_null(name)) {
			name = "image";
		}
		this.setAttribute("border", 0);
		this.setName(name);
		this.setAltText(name);
		this.setSrc(src);
		this.setWidth(width);
		this.setHeight(height);
		this.attr("class", styleclass);
	}

	public void setSrc(String value) {
		this.setAttribute("src", value);
	}
	
	public void setAltText(String value) {
		this.setAttribute("alt", value);
	}
	
	public void setMap(String value) {
		if(!value.startsWith("#")){
			value = "#"+value;
		}
		this.setAttribute("usemap", value);
	}
	
	public void setWidth(Integer value) {
		if (Cphp.is_null(value)) {
			this.removeAttribute("width");
		} else {
			this.setAttribute("width", value);
		}
	}
	
	public void setHeight(Integer value) {
		if (Cphp.is_null(value)) {
			this.removeAttribute("height");
		} else {
			this.setAttribute("height", value);
		}
	}
	
	public void preload() {
		String id = (String)getAttribute("id");
		if (Cphp.empty(id)) {
			id = "img"+System.currentTimeMillis();
			setAttribute("id", id);
		}

//		insert_js(
//				'jQuery('.CJs::encodeJson(_this->toString()).').load(function() {
//					var parent = jQuery("#'._id.'preloader").parent();
//					jQuery("#'._id.'preloader").remove();
//					jQuery(parent).append(jQuery(this));
//				});',
//				true
//			);

		this.addClass("preloader");
		this.setAttribute("id", id+"preloader");
		this.setAttribute("src", "styles/themes/originalblue/images/preloader.gif");
	}
}
