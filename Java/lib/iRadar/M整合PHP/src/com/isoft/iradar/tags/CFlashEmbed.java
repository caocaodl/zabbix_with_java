package com.isoft.iradar.tags;

import com.isoft.lang.CodeConfirmed;

@CodeConfirmed("benne.2.2.7")
public class CFlashEmbed extends CTag {

	private static final long serialVersionUID = 1L;

	public CFlashEmbed() {
		this(null);
	}

	public CFlashEmbed(String src) {
		this(src, null);
	}

	public CFlashEmbed(String src, Integer width) {
		this(src, width, null);
	}

	public CFlashEmbed(String src, Integer width, Integer height) {
		super("embed");
		this.attr("allowScriptAccess", "sameDomain");
		this.attr("type", "application/x-shockwave-flash");
		this.attr("pluginspage", "http://www.macromedia.com/go/getflashplayer");
		this.attr("align", "middle");
		this.attr("quality", "high");
		this.attr("wmode", "opaque");
		this.attr("width", width);
		this.attr("height", height);
		this.attr("src", src);
	}

	public void setWidth(Integer value) {
		this.attr("width", value);
	}

	public void setHeight(Integer value) {
		this.attr("height", value);
	}

	public void setSrc(String value) {
		this.attr("src", value);
	}

}
