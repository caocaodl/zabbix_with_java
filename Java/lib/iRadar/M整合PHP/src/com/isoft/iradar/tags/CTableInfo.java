package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.inc.JsUtil;

public class CTableInfo extends CTable {
	
	private static final long serialVersionUID = 1L;

	public CTableInfo() {
		this("...");
	}

	public CTableInfo(String message) {
		this(message, "tableinfo");
	}

	public CTableInfo(String message, String styleclass) {
		super(message, styleclass);
		this.setOddRowClass("odd_row");
		this.setEvenRowClass("even_row");
		this.attributes.put("cellpadding", 3);
		this.attributes.put("cellspacing", 1);
		this.headerClass = "header";
		this.footerClass = "footer";
	}

	public void makeVerticalRotation() {
		if (!Cphp.defined("IS_VERTICAL_ROTATION_JS_INSERTED")) {
			Cphp.define("IS_VERTICAL_ROTATION_JS_INSERTED", true);
			JsUtil.insert_js(JsUtil.getJsTemplate("VerticalRotation").replaceFirst("_JS_", (String)this.getAttribute("class")), true);
		}
	}
}
