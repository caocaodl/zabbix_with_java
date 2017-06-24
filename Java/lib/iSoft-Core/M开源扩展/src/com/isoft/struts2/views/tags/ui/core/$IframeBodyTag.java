package com.isoft.struts2.views.tags.ui.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $IframeBodyTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	private String _onload;
	private String _onunload;
	private String _oncontextmenu;
	private String _style;
	private String _styleClass;
	private String _narrow;
	private String _autoCrumb;

	public void setOnload(String onload) {
		this._onload = onload;
	}

	public void setOnunload(String onunload) {
		this._onunload = onunload;
	}

	public void setOncontextmenu(String oncontextmenu) {
		this._oncontextmenu = oncontextmenu;
	}

	public void setStyle(String style) {
		this._style = style;
	}
	
	public void setStyleClass(String styleClass) {
		this._styleClass = styleClass;
	}

	public void setNarrow(String narrow) {
		this._narrow = narrow;
	}
	
	public void setAutoCrumb(String autoCrumb) {
		this._autoCrumb = autoCrumb;
	}

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $IframeBody(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$IframeBody tagBean = ($IframeBody) component;
		if (this._onload != null && this._onload.length() > 0) {
			tagBean.setOnload(getPropertyStringValue(this._onload));
		}
		if (this._onunload != null && this._onunload.length() > 0) {
			tagBean.setOnunload(getPropertyStringValue(this._onunload));
		}
		if (this._oncontextmenu != null && this._oncontextmenu.length() > 0) {
			tagBean.setOncontextmenu(getPropertyStringValue(this._oncontextmenu));
		}
		if (this._style != null && this._style.length() > 0) {
			tagBean.setStyle(getPropertyStringValue(this._style));
		}
		if (this._styleClass != null && this._styleClass.length() > 0) {
			tagBean.setStyleClass(getPropertyStringValue(this._styleClass));
		}
		if (this._narrow != null && this._narrow.length() > 0) {
			tagBean.setNarrow(getPropertyBooleanValue(this._narrow));
		}
		if(this._autoCrumb!=null && this._autoCrumb.length()>0) {
			tagBean.setAutoCrumb(getPropertyBooleanValue(this._autoCrumb));
		}
	}

	@Override
	public void release() {
		super.release();
		this._onload = null;
		this._onunload = null;
		this._oncontextmenu = null;
		this._style = null;
		this._styleClass = null;
		this._narrow = null;
		this._autoCrumb = null;
	}
}
