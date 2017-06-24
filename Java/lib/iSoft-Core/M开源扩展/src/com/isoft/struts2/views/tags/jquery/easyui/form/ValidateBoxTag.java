package com.isoft.struts2.views.tags.jquery.easyui.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class ValidateBoxTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new ValidateBox(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		ValidateBox aui = (ValidateBox) component;
		if (styleClass != null && styleClass.length() > 0) {
			aui.setStyleClass(getPropertyStringValue(styleClass));
		}
		if (required != null && required.length() > 0) {
			aui.setRequired(getPropertyBooleanValue(required));
		}
		if (validType != null && validType.length() > 0) {
			aui.setValidType(getPropertyStringValue(validType));
		}
		if (delay != null && delay.length() > 0) {
			aui.setDelay(getPropertyIntegerValue(delay));
		}
		if (missingMessage != null && missingMessage.length() > 0) {
			aui.setMissingMessage(getPropertyStringValue(missingMessage));
		}
		if (invalidMessage != null && invalidMessage.length() > 0) {
			aui.setInvalidMessage(getPropertyStringValue(invalidMessage));
		}
		aui.setTipPosition(getPropertyStringValue(tipPosition,"top"));
		if (deltaX != null && deltaX.length() > 0) {
			aui.setDeltaX(getPropertyIntegerValue(deltaX));
		}
		if (novalidate != null && novalidate.length() > 0) {
			aui.setNovalidate(getPropertyBooleanValue(novalidate));
		}
	}

	@Override
	public void release() {
		super.release();
		this.styleClass = null;
		this.required = null;
		this.validType = null;
		this.delay = null;
		this.missingMessage = null;
		this.invalidMessage = null;
		this.tipPosition = null;
		this.deltaX = null;
		this.novalidate = null;
	}

	private String styleClass;
	private String required;
	private String validType;
	private String delay;
	private String missingMessage;
	private String invalidMessage;
	private String tipPosition;
	private String deltaX;
	private String novalidate;

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public void setValidType(String validType) {
		this.validType = validType;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public void setMissingMessage(String missingMessage) {
		this.missingMessage = missingMessage;
	}

	public void setInvalidMessage(String invalidMessage) {
		this.invalidMessage = invalidMessage;
	}

	public void setTipPosition(String tipPosition) {
		this.tipPosition = tipPosition;
	}

	public void setDeltaX(String deltaX) {
		this.deltaX = deltaX;
	}

	public void setNovalidate(String novalidate) {
		this.novalidate = novalidate;
	}

}
