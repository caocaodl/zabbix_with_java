package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.urlencode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Curl {

	private String url;
	protected String reference;
	protected String query;
	protected Map<Object, Object> arguments = new HashMap();
	
	public Curl() {
		this(null);
	}
	
	public Curl(String url) {
		HttpServletRequest request = RadarContext.getContext().getRequest();
		if (empty(url)) {
			this.formatGetArguments();
			this.url = request.getRequestURI();
		} else {
			this.url = url;
			int pos = this.url.indexOf('#');
			if (pos > -1) {
				if(pos<(this.url.length()-1)){
					this.reference = this.url.substring(pos+1);
				}
				this.url = this.url.substring(0, pos);
			}
			
			pos = this.url.indexOf('?');
			if (pos > -1) {
				if(pos<(this.url.length()-1)){
					this.query = this.url.substring(pos+1);
				}
				this.url = this.url.substring(0, pos);
			}
			
			this.formatArguments();
		}
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if ("rda_sessionid".equals(cookie.getName())) {
					this.setArgument("sid", cookie.getValue());
					break;
				}
			}
		}
	}

	public void formatQuery() {
		List<String> query = new ArrayList();
		for (Entry<Object, Object> entry : this.arguments.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (is_null(value)) {
				continue;
			}
			if (isArray(value)) {
				CArray cvalue= CArray.valueOf(value);
				for (Entry<Object, Object> e : ((CArray<Object>)cvalue).entrySet()) {
				    Object vkey = e.getKey();
				    Object vvalue = e.getValue();
				    if (isArray(vvalue)) {
				    	continue;
				    }
				    query.add(urlencode(key + "[" + Nest.as(vkey).asString() + "]") + "=" + urlencode(Nest.as(vvalue).asString()));
				}
			} else {
				query.add(urlencode(EasyObject.asString(key)) + "=" + urlencode(Nest.as(value).asString()));
			}
		}
		this.query = implode("&", query.toArray(new String[0]));
	}
	
	public void formatGetArguments() {
		HttpServletRequest request = RadarContext.getContext().getRequest();
		String method = request.getMethod();
		if ("GET".equals(method)) {
			this.arguments.putAll((Map)RadarContext._GET());
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if ("rda_sessionid".equals(cookie.getName())) {
					this.setArgument("sid", cookie.getValue());
					break;
				}
			}
		}
		this.formatQuery();
	}
	
	public void formatArguments() {
		this.formatArguments(null);
	}
	
	public void formatArguments(String query) {
		if (query == null) {
			query = this.query;
		}
		if (query != null) {
			String[] args = query.split("&");
			for (String arg : args) {
				if (empty(arg)) {
					continue;
				}
				if (arg.indexOf('=') > -1) {
					String[] vs = arg.split("=");
					String name = vs[0];
					String value = vs.length > 1 ? vs[1] : "";
					this.arguments.put(name, urlencode(value));
				} else {
					this.arguments.put(arg, "");
				}
			}
		}
		this.formatQuery();
	}
	
	public String getUrl() {
		this.formatQuery();
		StringBuilder url = new StringBuilder();
		url.append(this.url);
		if (this.query != null && this.query.length() > 0) {
			url.append('?').append(this.query);
		}
		if (this.reference != null && this.reference.length() > 0) {
			url.append('#').append(urlencode(this.reference));
		}
		return url.toString();
	}
	
	public void removeArgument(String key) {
		this.arguments.remove(key);
	}
	
	public void setArgument(String key) {
		this.setArgument(key, "");
	}

	public void setArgument(String key, Object value) {
		this.arguments.put(key, value);
	}

	public Object getArgument(String key) {
		if (isset(this.arguments, key)) {
			return this.arguments.get(key);
		}
		return null;
	}

	public void setQuery(String query) {
		this.query = query;
		this.formatArguments();
		this.formatQuery();
	}
	
	public String getQuery() {
		this.formatQuery();
		return this.query;
	}
	
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String toString() {
		return this.getUrl();
	}
}
