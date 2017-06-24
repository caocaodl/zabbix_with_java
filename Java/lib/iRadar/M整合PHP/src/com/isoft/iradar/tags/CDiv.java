package com.isoft.iradar.tags;

import static com.isoft.Feature.originalStyle;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.FuncsUtil.formatDomId;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;

public class CDiv extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CDiv() {
		this(null,null,null);
	}
	
	public CDiv(Object items) {
		this(items,null,null);
	}
	
	public CDiv(Object items, String styleclass) {
		this(items,styleclass,null);
	}
	
	public CDiv(Object items, String styleclass, String id) {
		super("div", "yes");
		this.attr("class", styleclass);
		if (!empty(id)) {
			this.attr("id", formatDomId(id));
		}
		this.addItem(items);

		this.tag_body_start = "";
		this.tag_start = "";
		this.tag_end = "";
		this.tag_body_end = "";
	}
	
	public CDiv useJQueryStyle() {
		if (originalStyle) {
			String styleclass = (String)this.getAttribute("class");
			this.setAttribute("class", styleclass+" jqueryinputset");
			if(!defined("RDA_JQUERY_INPUTSET")){
				define("RDA_JQUERY_INPUTSET", true);
				rda_add_post_js("setTimeout(function() { jQuery(\"div.jqueryinputset\").buttonset(); }, 10);");
			}
		}
		return this;
	}
}
