package com.isoft.struts2.views.tags.jquery.easyui.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class ComboBox extends Combo {

	public ComboBox(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected String getFunc() {
		return "combobox";
	}

	@Override
	protected void renderProperties(HtmlResponseWriter writer)
			throws IOException {
		super.renderProperties(writer);
		if (valueField != null && valueField.length() > 0) {
			writer.writeLine("		valueField:'" + valueField + "',");
		}
		if (textField != null && textField.length() > 0) {
			writer.writeLine("		textField:'" + textField + "',");
		}
		if (groupField != null && groupField.length() > 0) {
			writer.writeLine("		groupField:'" + groupField + "',");
		}
		if (groupFormatter != null && groupFormatter.length() > 0) {
			writer.writeLine("		groupFormatter:'" + groupFormatter + "',");
		}
		if (mode != null && mode.length() > 0) {
			writer.writeLine("		mode:'" + mode + "',");
		}
		if (url != null && url.length() > 0) {
			writer.writeLine("		url:'" + url + "',");
		}
		if (method != null && method.length() > 0) {
			writer.writeLine("		method:'" + method + "',");
		}
		if (data != null && data.length() > 0) {
			writer.writeLine("		data:" + data + ",");
		}
		if (filter != null && filter.length() > 0) {
			writer.writeLine("		filter:'" + filter + "',");
		}
		if (formatter != null && formatter.length() > 0) {
			writer.writeLine("		formatter:'" + formatter + "',");
		}
		if (loader != null && loader.length() > 0) {
			writer.writeLine("		loader:'" + loader + "',");
		}
		if (loadFilter != null && loadFilter.length() > 0) {
			writer.writeLine("		loadFilter:'" + loadFilter + "',");
		}
	}

	private String valueField;
	private String textField;
	private String groupField;
	private String groupFormatter;
	private String mode;
	private String url;
	private String method;
	private String data;
	private String filter;
	private String formatter;
	private String loader;
	private String loadFilter;

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public void setGroupField(String groupField) {
		this.groupField = groupField;
	}

	public void setGroupFormatter(String groupFormatter) {
		this.groupFormatter = groupFormatter;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}

	public void setLoadFilter(String loadFilter) {
		this.loadFilter = loadFilter;
	}

}
