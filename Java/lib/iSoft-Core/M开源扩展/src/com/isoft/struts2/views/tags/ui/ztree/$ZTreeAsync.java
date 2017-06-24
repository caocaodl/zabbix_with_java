package com.isoft.struts2.views.tags.ui.ztree;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.model.TreeAsyncItem;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.TreeSettingHolder;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $ZTreeAsync extends AndurilUIComponent {

	public $ZTreeAsync(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected void popComponentStack() {
		super.popComponentStack();
		Object component = this.getComponentStack().peek();
		if (component instanceof TreeSettingHolder) {
			TreeAsyncItem i = new TreeAsyncItem();
			i.setAutoParam(autoParam);
			i.setContentType(contentType);
			i.setDataFilter(dataFilter);
			i.setDataType(dataType);
			i.setEnable(enable);
			i.setOtherParam(otherParam);
			i.setType(type);
			i.setUrl(url);
			((TreeSettingHolder) component).pushTreeAsyncItem(i);
		}
	}

	private String autoParam;
	private String contentType;
	private String dataFilter;
	private String dataType;
	private boolean enable;
	private String otherParam;
	private String type;
	private String url;

	public String getAutoParam() {
		return autoParam;
	}

	public void setAutoParam(String autoParam) {
		this.autoParam = autoParam;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getDataFilter() {
		return dataFilter;
	}

	public void setDataFilter(String dataFilter) {
		this.dataFilter = dataFilter;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean getEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getOtherParam() {
		return otherParam;
	}

	public void setOtherParam(String otherParam) {
		this.otherParam = otherParam;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
