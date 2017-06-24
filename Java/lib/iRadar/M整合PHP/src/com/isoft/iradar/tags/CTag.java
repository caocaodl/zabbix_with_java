package com.isoft.iradar.tags;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.Feature;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.utils.CJs;

public class CTag extends CObject {

	private static final long serialVersionUID = 1L;

/**
	 * Encodes the '<', '>', '"' and '&' symbols.
	 */
	public final static int ENC_ALL = 1;

	/**
	 * Encodes all symbols in ENC_ALL except for '&'.
	 */
	public final static int ENC_NOAMP = 2;

	/**
	 * The HTML encoding strategy to use for the contents of the tag.
	 */
	protected int encStrategy = ENC_NOAMP;

	/**
	 * The HTML encoding strategy for the "value", "name" and "id" attributes.
	 */
	protected int attrEncStrategy = ENC_ALL;

	protected Map<String, Object> attributes = new HashMap<String, Object>();
	protected Map<String, Object> dataAttributes = new HashMap<String, Object>();

	protected String tagName;
	protected String paired;

	protected String tag_start;
	protected String tag_end;
	protected String tag_body_start;
	protected String tag_body_end;

	public CTag() {
		this(null, null, null, null);
	}
	
	public CTag(String tagName) {
		this(tagName, null, null, null);
	}
	
	public CTag(String tagName, String paired) {
		this(tagName, paired, null, null);
	}
	
	public CTag(String tagName, String paired, Object body) {
		this(tagName, paired, body, null);
	}
	
	public CTag(String tagName, String paired, Object body, String styleClass) {
		super();
		if(paired == null){
			paired = "no";
		}
		this.tagName = tagName;
		this.paired = paired;
		this.tag_start = this.tag_end = this.tag_body_start = this.tag_body_end = "";

		if (Cphp.is_null(body)) {
			this.tag_end = this.tag_body_start = "";
		} else {
			this.addItem(body);
		}
		this.addClass(styleClass);
	}
	
	public StringBuilder startToString(){
		StringBuilder res = new StringBuilder();
		res.append(this.tag_start).append('<').append(this.tagName);
		for(Entry<String, Object> e:this.attributes.entrySet()){
			String key = e.getKey();
			Object value = e.getValue();
			if(value == null){
				continue;
			}
			int strategy = Cphp.strInArray(key, new String[]{"value","name","id"})?this.attrEncStrategy:this.encStrategy;
			value = this.encode(attrToString(value), strategy);
			res.append(' ').append(key).append('=').append('"').append(value).append('"');
		}
		res.append("yes".equals(this.paired)?">":"/>");
		return res;
	}
	
	public StringBuilder bodyToString(){
		StringBuilder res = new StringBuilder();
		res.append(this.tag_body_start);
		res.append(super.toString(false));
		return res;
	}
	
	public StringBuilder endToString(){
		StringBuilder res = new StringBuilder();
		if("yes".equals(this.paired)){
			res.append(this.tag_body_end).append("</").append(this.tagName).append('>');
		}
		res.append(this.tag_end);
		return res;
	}
	
	private String attrToString(Object value) {
		if(value instanceof Map){
			return CJs.encodeJson(value);
		} else {
			return value.toString();
		}		
	}
	
	@Override
	public String toString(Boolean destroy) {
		if(destroy == null){
			destroy = true;
		}
		StringBuilder res = this.startToString();
		res.append(this.bodyToString());
		res.append(this.endToString());
		if (destroy) {
			this.destroy();
		}
		return res.toString();
	}
	
	@Override
	public CObject addItem(Object value){
		if(value instanceof String){
			value = this.encode((String)value, this.getEncStrategy());
		}
		super.addItem(value);
		return this;
	}

	public void setName(String value) {
		if (Cphp.is_null(value)) {
			return;
		}
		this.setAttribute("name", value);
	}

	public String getName() {
		if (Cphp.isset(this.attributes, "name")) {
			return (String) this.attributes.get("name");
		}
		return null;
	}

	public String addClass(String cssClass) {
		if (Cphp.is_null(this.attributes.get("class")) || Cphp.empty(this.attributes.get("class"))) {
			this.attributes.put("class", cssClass);
		} else {
			this.attributes.put("class", (String) this.attributes.get("class") + " " + cssClass);
		}
		return (String) this.attributes.get("class");
	}

	public boolean hasClass(String cssClass) {
		String[] chkClass = Cphp.explode(" ",
				(String) this.getAttribute("class"));
		return Cphp.strInArray(cssClass, chkClass);
	}

	public void attr(String name, Object value) {
		this.setAttribute(name, value);
	}

	public Object getAttribute(String name) {
		return attributes.containsKey(name) ? attributes.get(name) : null;
	}

	public void setAttribute(String name, Object value) {
		if (!Cphp.is_null(value)) {
			this.attributes.put(name, value);
		} else {
//			this.removeAttribute(name);
		}
	}

	public void setAttributes(Map<String, Object> attributes) {
		if (!attributes.isEmpty()) {
			for (Entry<String, Object> e : attributes.entrySet()) {
				this.setAttribute(e.getKey(), e.getValue());
			}
		}
	}

	public void removeAttr(String name) {
		this.removeAttribute(name);
	}

	public void removeAttribute(String name) {
		this.attributes.remove(name);
	}

	public void addAction(String name, String value) {
		this.attributes.put(name, value);
	}

	public void setMenuPopup(Map data) {
		if(Feature.showPopupMenu) {
			this.attr("data-menu-popup", CJs.encodeJson(data));
		}
	}

	public void onClick(String handleCode) {
		this.addAction("onclick", handleCode);
	}

	public void addStyle(String value) {
		if (!Cphp.isset(this.attributes,"style")) {
			this.attributes.put("style", "");
		}
		if (Cphp.isset(value)) {
			this.attributes.put("style",(String)this.attributes.get("style")+CHtml.htmlspecialchars(value));
		} else {
			Cphp.unset(this.attributes,"style");
		}
	}
	
	public <T extends CTag> T setEnabled(Boolean enabled){
		if(enabled != null) {
			if(enabled){
				Cphp.unset(this.attributes, "disabled");
			} else {
				this.attributes.put("disabled", "disabled");
			}
		}
		return (T) this;
	}

	public int error(Object _value) {
		FuncsUtil.error("class("+this.getClass()+") - "+_value);
		return 1;
	}

	public void getForm() {
		// TODO
	}

	public void setTitle(String title) {
		this.setAttribute("title", title);
	}

	protected String encode(String value, int strategy) {
		if (value != null && value.length() > 0) {
			if (ENC_NOAMP == strategy) {
				value = value.replaceAll("<", "&lt;");
				value = value.replaceAll(">", "&gt;");
				value = value.replaceAll("\"", "&quot;");
			} else {
				value = CHtml.encode(value);
			}
		}
		return value;
	}

	public void setEncStrategy(int encStrategy) {
		this.encStrategy = encStrategy;
	}

	public int getEncStrategy() {
		return this.encStrategy;
	}
	
	
	public void setHint(Object _text) {
		this.setHint(_text, "", "", true ,true);
	}
	
	public boolean setHint(Object _text, String _width, String _class) {
		return this.setHint(_text, _width, _class, true, true);
	}
	
	public boolean setHint(Object _text, String _width, String _class, boolean _byClick) {
		return this.setHint(_text, _width, _class, _byClick, true);
	}
	
	public boolean setHint(Object _text, String _width, String _class, boolean _byClick, boolean _encode) {
		if (Cphp.empty(_text)) {
			return false;
		}

		_text = JsUtil.encodeValues(_text);
		String text = unpack_object(_text).toString();

		this.addAction("onmouseover", "hintBox.HintWraper(event, this, "+JsUtil.rda_jsvalue(text)+", \""+_width+"\", \""+_class+"\");");
		if (_byClick) {
			this.addAction("onclick", "hintBox.showStaticHint(event, this, "+JsUtil.rda_jsvalue(text)+", \""+_width+"\", \""+_class+"\");");
		}
		return true;
	}
}
