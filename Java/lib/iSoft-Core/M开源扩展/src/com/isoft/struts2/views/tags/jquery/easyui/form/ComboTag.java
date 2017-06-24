package com.isoft.struts2.views.tags.jquery.easyui.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.opensymphony.xwork2.util.ValueStack;

public class ComboTag extends ValidateBoxTag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new Combo(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		Combo aui = (Combo) component;
		if (width != null && width.length() > 0) {
			aui.setWidth(getPropertyIntegerValue(width));
		}
		if (height != null && height.length() > 0) {
			aui.setHeight(getPropertyIntegerValue(height));
		}
		if (panelWidth != null && panelWidth.length() > 0) {
			aui.setPanelWidth(getPropertyIntegerValue(panelWidth));
		}
		if (panelHeight != null && panelHeight.length() > 0) {
			aui.setPanelHeight(getPropertyIntegerValue(panelHeight));
		}
		if (multiple != null && multiple.length() > 0) {
			aui.setMultiple(getPropertyBooleanValue(multiple));
		}
		if (selectOnNavigation != null && selectOnNavigation.length() > 0) {
			aui.setSelectOnNavigation(getPropertyBooleanValue(selectOnNavigation));
		}
		if (separator != null && separator.length() > 0) {
			aui.setSeparator(getPropertyStringValue(separator));
		}
		if (editable != null && editable.length() > 0) {
			aui.setEditable(getPropertyBooleanValue(editable));
		}
		if (disabled != null && disabled.length() > 0) {
			aui.setDisabled(getPropertyBooleanValue(disabled));
		}
		if (readonly != null && readonly.length() > 0) {
			aui.setReadonly(getPropertyBooleanValue(readonly));
		}
		if (hasDownArrow != null && hasDownArrow.length() > 0) {
			aui.setHasDownArrow(getPropertyBooleanValue(hasDownArrow));
		}
		if (keyHandler != null && keyHandler.length() > 0) {
			aui.setKeyHandler(getPropertyStringValue(keyHandler));
		}
	}

	@Override
	public void release() {
		super.release();
		this.width = null;
		this.height = null;
		this.panelWidth = null;
		this.panelHeight = null;
		this.multiple = null;
		this.selectOnNavigation = null;
		this.separator = null;
		this.editable = null;
		this.disabled = null;
		this.readonly = null;
		this.hasDownArrow = null;
		this.keyHandler = null;
	}

	private String width;
	private String height;
	private String panelWidth;
	private String panelHeight;
	private String multiple;
	private String selectOnNavigation;
	private String separator;
	private String editable;
	private String disabled;
	private String readonly;
	private String hasDownArrow;
	private String keyHandler;

	public void setWidth(String width) {
		this.width = width;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setPanelWidth(String panelWidth) {
		this.panelWidth = panelWidth;
	}

	public void setPanelHeight(String panelHeight) {
		this.panelHeight = panelHeight;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}

	public void setSelectOnNavigation(String selectOnNavigation) {
		this.selectOnNavigation = selectOnNavigation;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public void setHasDownArrow(String hasDownArrow) {
		this.hasDownArrow = hasDownArrow;
	}

	public void setKeyHandler(String keyHandler) {
		this.keyHandler = keyHandler;
	}

}
