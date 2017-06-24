package com.isoft.iradar.tags;

import static com.isoft.Feature.originalStyle;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.types.CArray;

public class CInput extends CTag {
	
	private static final long serialVersionUID = 1L;

	protected boolean jQuery;
	
	public CInput() {
		this("text");
	}
	
	public CInput(String type) {
		this(type, "textbox");
	}
	
	public CInput(String type, String name) {
		this(type, name, "");
	}
	
	public CInput(String type, String name, String value) {
		this(type, name, value, null);
	}
	
	public CInput(String type, String name, String value, String styleclass) {
		this(type, name, value, styleclass, null);
	}
	
	public CInput(String type, String name, String value, String styleclass, String id) {
		super("input", "no");
		if (type == null) {
			type = "text";
		}
		if (name == null) {
			name = "textbox";
		}
		if (value == null) {
			value = "";
		}
		this.jQuery = false;
		this.setType(type);
		if(Cphp.is_null(id)){
			this.attr("id", FuncsUtil.formatDomId(name));
		} else {
			this.attr("id", FuncsUtil.formatDomId(id));
		}
		this.attr("name", name);
		this.attr("value", value);
		styleclass = !Cphp.is_null(styleclass)?styleclass:type;
		if("button".equals(styleclass) || "submit".equals(styleclass)){
			styleclass += " shadow ui-corner-all";
		}
		this.addClass("input "+styleclass);
	}

	public CInput setType(String type) {
		this.attr("type", type);
		return this;
	}
	
	public CInput setReadonly(Boolean readonly) {
		if(readonly == null){
			readonly = true;
		}		
		if(readonly){
			this.attr("readonly", "readonly");
			return this;
		}
		this.removeAttr("readonly");
		return this;
	}
	
	public CInput setEnabled(Boolean enabled) {
		if(enabled == null){
			enabled = true;
		}		
		if(enabled){
			this.removeAttr("disabled");			
			return this;
		}
		this.attr("disabled", "disabled");
		return this;
	}
	
	public CInput setEnabled(String $value) {
		boolean enabled = Cphp.in_array($value, CArray.array("yes", "checked", "on", "1"));
		return setEnabled(enabled);
	}
	
	public CInput useJQueryStyle() {
		if (originalStyle) {
			return useJQueryStyle("");
		} else {
			return this;
		}
	}
	public CInput useJQueryStyle(String _class) {
		if (originalStyle) {
			this.jQuery = true;
			this.attr("class", "jqueryinput "+this.getAttribute("class")+" "+_class);
			if (!Cphp.defined("RDA_JQUERY_INPUT")) {
				Cphp.define("RDA_JQUERY_INPUT", true);
				JsUtil.rda_add_post_js("jQuery(\"input.jqueryinput\").button();");
			}
		}
		return this;
	}
}
