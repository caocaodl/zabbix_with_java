package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.is_string;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;

import java.util.Map;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;

public class CButtonQMessage extends CSubmit {
	
	private static final long serialVersionUID = 1L;
	
	private String vars;
	private String msg;
	private String name;
	
	public CButtonQMessage(String name, String caption) {
		
		this(name, caption, null);
	}
	
	public CButtonQMessage(String name, String caption, String msg) {
		this(name, caption, msg, null);
	}
	
	public CButtonQMessage(String name, String caption, String msg, String vars) {
		this(name, caption, msg, vars, null);
	}

	public CButtonQMessage(String name, String caption, String msg, String vars, String styleclass) {
		super(name, caption, null, styleclass);
		this.vars = null;
		this.msg = null;
		this.name = name;
		this.setMessage(msg);
		this.setVars(vars);
		this.setAction(null);
	}
	
	public void setVars(String value) {
		this.vars = value;
		this.setAction(null);
	}
	
	public void setMessage(String value) {
		if (Cphp.is_null(value)) {
			value = Cphp._("Are you sure you want perform this action?");
		}
		
		if (!is_string(value)) {
			this.error(_s("Incorrect value for setMessage(): \"%s\".", value));
		}
		
		// if message will contain single quotes, it will break everything, so it must be escaped
		this.msg = rda_jsvalue(
			value,
			false, // not as object
			false // do not add quotes to the string
		);
		this.setAction(null);
	}
	
	public void setAction(String value) {
		if (!Cphp.is_null(value)) {
			setAttribute("onclick", value);
		}

		Map<String, Object> page = RadarContext.getContext().getPage();
		//String confirmation = "Confirm('" + this.msg + "')";

		String action;
		if (Cphp.isset(this.vars)) {
			String link = (String) page.get("file") + "?" + this.name + "=1" + this.vars;
			Curl url = new Curl(link);
			action = "redirect('" + url.getUrl() + "')";
		} else {
			action = "true";
		}
		setAttribute("type", "button");
		//setAttribute("onclick", "if (" + confirmation + ") { return " + action + "; } else { return false; }");
		setAttribute("onclick","showModalWindow('"+_("Caption")+"',\""+ this.msg +"\",[{"+ "text:'"+_("Sure")+"',click:function(){  return " + action + "}"
				+ "},{text:'"+_("Cancel")+"',click:function(){ jQuery(this).dialog('destroy') }}])");
	}
}