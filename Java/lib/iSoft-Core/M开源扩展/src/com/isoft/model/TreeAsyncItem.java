package com.isoft.model;

import java.io.Serializable;

public class TreeAsyncItem implements Serializable {

	private static final long serialVersionUID = 1L;

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
