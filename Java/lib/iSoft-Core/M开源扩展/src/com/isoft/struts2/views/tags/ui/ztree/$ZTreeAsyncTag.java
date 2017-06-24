package com.isoft.struts2.views.tags.ui.ztree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $ZTreeAsyncTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $ZTreeAsync(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$ZTreeAsync aui = ($ZTreeAsync) component;
		aui.setAutoParam(autoParam);
		aui.setContentType(contentType);
		aui.setDataFilter(dataFilter);
		aui.setDataType(dataType);
		aui.setEnable(getPropertyBooleanValue(enable));
		aui.setOtherParam(otherParam);
		aui.setType(type);
		aui.setUrl(url);
	}

	@Override
	public void release() {
		super.release();
		this.autoParam = null;
		this.contentType = null;
		this.dataFilter = null;
		this.dataType = null;
		this.enable = null;
		this.otherParam = null;
		this.type = null;
		this.url = null;
	}

	private String autoParam;
	private String contentType;
	private String dataFilter;
	private String dataType;
	private String enable;
	private String otherParam;
	private String type;
	private String url;

	public void setAutoParam(String autoParam) {
		this.autoParam = autoParam;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setDataFilter(String dataFilter) {
		this.dataFilter = dataFilter;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public void setOtherParam(String otherParam) {
		this.otherParam = otherParam;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
