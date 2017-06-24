package com.isoft.iradar.tags;

import java.util.HashMap;
import java.util.Map;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.inc.JsUtil;

public class CTextArea extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CTextArea() {
		this("textarea");
	}
	
	public CTextArea(String name) {
		this(name, "");
	}
	
	public CTextArea(String name, String value) {
		this(name, value, new HashMap(0));
	}
	
	public CTextArea(String name, String value, Map<String, Object> options) {
		super("textarea", "yes");
		this.attr("class", "input");
		this.attr("id", FuncsUtil.formatDomId(name));
		this.attr("name", name);
		this.attr("rows", !Cphp.empty(options.get("rows")) ? options.get("rows")
				: Defines.RDA_TEXTAREA_STANDARD_ROWS);
		this.setReadonly(!Cphp.empty(options.get("readonly")));
		this.addItem(value);

		Integer width = (Integer) options.get("width");
		if (Cphp.empty(width)
				|| width == Defines.RDA_TEXTAREA_STANDARD_WIDTH) {
			this.addClass("textarea_standard");
		} else if (width == Defines.RDA_TEXTAREA_BIG_WIDTH) {
			this.addClass("textarea_big");
		} else {
			this.attr("style", "width:" + width + "px");
		}

		if (!Cphp.empty(options.get("maxlength"))) {
			this.setMaxlength(options.get("maxlength"));
		}
	}

	public void setReadonly() {
		this.setReadonly(true);
	}

	public void setReadonly(boolean value) {
		if (value) {
			this.attr("readonly", "readonly");
		} else {
			this.removeAttribute("readonly");
		}
	}

	public void setValue() {
		this.setValue("");
	}

	public void setValue(Object value) {
		this.addItem(value);
	}

	public void setRows(Object value) {
		this.attr("rows", value);
	}

	public void setCols(Object value) {
		this.attr("cols", value);
	}

	public void setMaxlength(Object maxlength) {
		this.attr("maxlength", maxlength);
		if (!Cphp.defined("IS_TEXTAREA_MAXLENGTH_JS_INSERTED")) {
			Cphp.define("IS_TEXTAREA_MAXLENGTH_JS_INSERTED", true);
			JsUtil.insert_js(
					"if (!CR && !GK) {"
							+ "	jQuery(\"textarea[maxlength]\").bind(\"paste contextmenu change keydown keypress keyup\", function() {"
							+ "		var elem = jQuery(this);"
							+ "		if (elem.val().length > elem.attr(\"maxlength\")) {"
							+ "			elem.val(elem.val().substr(0, elem.attr(\"maxlength\")));"
							+ "		}" 
							+ "	});" 
							+ "}", true);
		}
	}
}
