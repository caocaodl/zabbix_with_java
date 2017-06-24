package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.FuncsUtil.get_request;

import java.util.Map;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.types.Mapper.Nest;

public class CForm extends CTag {
	
	private static final long serialVersionUID = 1L;

	public CForm() {
		this("post");
	}
	
	public CForm(String method) {
		this(method, null);
	}
	
	public CForm(String method, String action) {
		this(method, action, null);
	}

	public CForm(String method, String action, String enctype) {
		super("form", "yes");
		this.setMethod(method);
		this.setAction(action);
		this.setEnctype(enctype);
		this.setAttribute("accept-charset", "utf-8");
		Map COOKIE = RadarContext._COOKIES();
		if (isset(COOKIE,"rda_sessionid")) {
			this.addVar("sid", Cphp.substr(Nest.value(COOKIE,"rda_sessionid").asString(), 16, 16));
		}
		this.addVar("form_refresh", get_request("form_refresh", 0) + 1);
	}

	public void setMethod() {
		this.setMethod("post");
	}

	public void setMethod(String value) {
		this.setAttribute("method", value);
	}

	public void setAction(String value) {
		if (is_null(value)) {
			Map<String, Object> page = RadarContext.getContext().getPage();
			if (isset(page, "file")) {
				value = (String) page.get("file");
			} else {
				value = "#";
			}
		}
		this.setAttribute("action", value);
	}

	public void setEnctype() {
		this.setEnctype(null);
	}

	public void setEnctype(String value) {
		if (is_null(value)) {
			this.removeAttribute("enctype");
		}
		this.setAttribute("enctype", value);
	}
	
	public void addVar(String name, Object value){
		this.addVar(name, value, null);
	}

	public void addVar(String name, Object value, String id) {
		if (!is_null(value)) {
			this.addItem(new CVar(name, value, id));
		}
	}
}
