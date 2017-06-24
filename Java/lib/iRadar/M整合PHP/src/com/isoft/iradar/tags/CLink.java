package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.*;
import static com.isoft.iradar.inc.FuncsUtil.*;
import java.util.Map;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.types.Mapper.Nest;

public class CLink extends CTag {
	
	private static final long serialVersionUID = 1L;

	private String sid;
	private Object nosid;
	
	public CLink() {
		this(null,null,null,null,null);
	}
	
	public CLink(Object item) {
		this(item,null,null,null,null);
	}
	
	public CLink(Object item, String url) {
		this(item,url,null,null,null);
	}
	
	public CLink(Object item, String url, String styleclass) {
		this(item,url,styleclass,null,null);
	}
	
	public CLink(Object item, String url, String styleclass, String action) {
		this(item,url,styleclass,action,null);
	}

	public CLink(Object item, String url, String styleclass, String action, Object nosid) {
		super("a", "yes");
		this.tag_start = "";
		this.tag_end = "";
		this.tag_body_start = "";
		this.tag_body_end = "";
		this.nosid = nosid;

		if (!Cphp.is_null(styleclass)) {
			this.setAttribute("class", styleclass);
		}

		if (!Cphp.is_null(item)) {
			this.addItem(item);
		}

		if (!Cphp.is_null(url)) {
			this.setUrl(url);
		}

		if (!Cphp.is_null(action)) {
			this.setAttribute("onclick", action);
		}
	}

	public void setUrl(String value) {
		if (is_null(this.nosid)) {
			if (is_null(this.sid)) {
				Map COOKIE = RadarContext._COOKIES();
				this.sid = isset(COOKIE,"rda_sessionid") ? substr(Nest.value(COOKIE, "rda_sessionid").asString(), 16, 16) : null;
			}
			if (!is_null(this.sid)) {
				value += (!empty(rda_strstr(value, "&")) || !empty(rda_strstr(value, "?")))
					? "&sid="+this.sid
					: "?sid="+this.sid;
			}
		}
		this.setAttribute("href", value);
	}

	public String getUrl() {
		return Cphp.isset(this.attributes, "href") ? (String) this.attributes.get("href") : null;
	}

	public void setTarget(String value) {
		if (Cphp.is_null(value)) {
			Cphp.unset(this.attributes, "target");
		} else {
			this.attributes.put("target", value);
		}
	}
}
