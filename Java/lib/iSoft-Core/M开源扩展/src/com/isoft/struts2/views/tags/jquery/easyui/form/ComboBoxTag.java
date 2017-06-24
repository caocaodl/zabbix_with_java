package com.isoft.struts2.views.tags.jquery.easyui.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.opensymphony.xwork2.util.ValueStack;

public class ComboBoxTag extends ComboTag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new ComboBox(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		ComboBox aui = (ComboBox) component;

		if (valueField != null && valueField.length() > 0) {
			aui.setValueField(getPropertyStringValue(valueField));
		}
		if (textField != null && textField.length() > 0) {
			aui.setTextField(getPropertyStringValue(textField));
		}
		if (groupField != null && groupField.length() > 0) {
			aui.setGroupField(getPropertyStringValue(groupField));
		}
		if (groupFormatter != null && groupFormatter.length() > 0) {
			aui.setGroupFormatter(getPropertyStringValue(groupFormatter));
		}
		if (mode != null && mode.length() > 0) {
			aui.setMode(getPropertyStringValue(mode));
		}
		if (url != null && url.length() > 0) {
			aui.setUrl(getPropertyStringValue(url));
		}
		if (method != null && method.length() > 0) {
			aui.setMethod(getPropertyStringValue(method));
		}
		if (data != null && data.length() > 0) {
			aui.setData(getPropertyStringValue(data));
		}
		if (filter != null && filter.length() > 0) {
			aui.setFilter(getPropertyStringValue(filter));
		}
		if (formatter != null && formatter.length() > 0) {
			aui.setFormatter(getPropertyStringValue(formatter));
		}
		if (loader != null && loader.length() > 0) {
			aui.setLoader(getPropertyStringValue(loader));
		}
		if (loadFilter != null && loadFilter.length() > 0) {
			aui.setLoadFilter(getPropertyStringValue(loadFilter));
		}
	}

	@Override
	public void release() {
		super.release();
		this.valueField = null;
		this.textField = null;
		this.groupField = null;
		this.groupFormatter = null;
		this.mode = null;
		this.url = null;
		this.method = null;
		this.data = null;
		this.filter = null;
		this.formatter = null;
		this.loader = null;
		this.loadFilter = null;
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
