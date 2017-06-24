package com.isoft.struts2.views.tags.jquery.easyui.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class Combo extends ValidateBox {

	public Combo(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected String getFunc() {
		return "combo";
	}

	@Override
	protected void renderProperties(HtmlResponseWriter writer)
			throws IOException {
		super.renderProperties(writer);
		if (width != null) {
			writer.writeLine("		width:" + width + ",");
		}
		if (height != null) {
			writer.writeLine("		height:" + height + ",");
		}
		if (panelWidth != null) {
			writer.writeLine("		panelWidth:" + panelWidth + ",");
		}
		if (panelHeight != null) {
			writer.writeLine("		panelHeight:" + panelHeight + ",");
		}
		if (multiple != null) {
			writer.writeLine("		multiple:" + multiple + ",");
		}
		if (selectOnNavigation != null) {
			writer.writeLine(" selectOnNavigation:" + selectOnNavigation + ",");
		}
		if (separator != null && separator.length() > 0) {
			writer.writeLine("		separator:'" + separator + "',");
		}
		if (editable != null) {
			writer.writeLine("		editable:" + editable + ",");
		}
		if (disabled != null) {
			writer.writeLine("		disabled:" + disabled + ",");
		}
		if (readonly != null) {
			writer.writeLine("		readonly:" + readonly + ",");
		}
		if (hasDownArrow != null) {
			writer.writeLine("		hasDownArrow:" + hasDownArrow + ",");
		}
		if (keyHandler != null && keyHandler.length() > 0) {
			writer.writeLine("		keyHandler:" + keyHandler + ",");
		}
	}

	private Integer width;
	private Integer height;
	private Integer panelWidth;
	private Integer panelHeight;
	private Boolean multiple;
	private Boolean selectOnNavigation;
	private String separator;
	private Boolean editable;
	private Boolean disabled;
	private Boolean readonly;
	private Boolean hasDownArrow;
	private String keyHandler;

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setPanelWidth(Integer panelWidth) {
		this.panelWidth = panelWidth;
	}

	public void setPanelHeight(Integer panelHeight) {
		this.panelHeight = panelHeight;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}

	public void setSelectOnNavigation(Boolean selectOnNavigation) {
		this.selectOnNavigation = selectOnNavigation;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public void setHasDownArrow(Boolean hasDownArrow) {
		this.hasDownArrow = hasDownArrow;
	}

	public void setKeyHandler(String keyHandler) {
		this.keyHandler = keyHandler;
	}

}
