package com.isoft.iradar.tags;

import com.isoft.lang.CodeConfirmed;

@CodeConfirmed("benne.2.2.6")
public class CFlash extends CTag {

	private static final long serialVersionUID = 1L;
	
	private CParam srcParam;
	private CFlashEmbed embededFlash;

	public CFlash() {
		this(null);
	}
	
	public CFlash(String src) {
		this(src, null);
	}
	
	public CFlash(String src, Integer width) {
		this(src, width, null);
	}
	
	public CFlash(String src, Integer width, Integer height) {
		super("object", "yes");
		this.attr("classid", "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000");
		this.attr("codebase", "http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0");
		this.attr("align", "middle");

		this.addItem(new CParam("allowScriptAccess", "sameDomain"));
		this.addItem(new CParam("quality", "high"));
		this.addItem(new CParam("wmode", "opaque"));
		
		this.srcParam = new CParam("movie", src);
		this.embededFlash = new CFlashEmbed();

		this.setWidth(width);
		this.setHeight(height);
		this.setSrc(src);
	}

	public void setWidth(Integer value) {
		this.attr("width", value);
		this.embededFlash.attr("width", value);
	}

	public void setHeight(Integer value) {
		this.attr("height", value);
		this.embededFlash.attr("height", value);
	}

	public void setSrc(String value) {
		this.srcParam.attr("value", value);
		this.embededFlash.attr("src", value);
	}

	@Override
	public StringBuilder bodyToString() {
		StringBuilder ret = super.bodyToString();
		ret.append(this.srcParam.toString());
		ret.append(this.embededFlash.toString());
		return ret;
	}
}
