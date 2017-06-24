package com.isoft.struts2.views.tags.ui.ztree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $ZTreeNodeTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $ZTreeNode(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);

		$ZTreeNode aui = ($ZTreeNode) component;
		aui.setId(getPropertyStringValue(getId()));
		aui.setpId(getPropertyStringValue(pId));
		aui.setChecked(getPropertyBooleanValue(checked));
		aui.setChkDisabled(getPropertyBooleanValue(chkDisabled));
		aui.setClick(getPropertyStringValue(click));
		aui.setHalfCheck(getPropertyBooleanValue(halfCheck));
		aui.setIcon(getPropertyStringValue(icon));
		aui.setIconClose(getPropertyStringValue(iconClose));
		aui.setIconOpen(getPropertyStringValue(iconOpen));
		aui.setIconSkin(getPropertyStringValue(iconSkin));
		aui.setIsHidden(getPropertyBooleanValue(isHidden));
		aui.setIsParent(getPropertyBooleanValue(isParent));
		aui.setName(getPropertyStringValue(name));
		aui.setNoCheck(getPropertyBooleanValue(noCheck));
		aui.setOpen(getPropertyBooleanValue(open));
		aui.setTarget(getPropertyStringValue(target));
		aui.setUrl(getPropertyStringValue(url));
	}

	@Override
	public void release() {
		super.release();
		this.pId = null;
		this.checked = null;
		this.chkDisabled = null;
		this.click = null;
		this.halfCheck = null;
		this.icon = null;
		this.iconClose = null;
		this.iconOpen = null;
		this.iconSkin = null;
		this.isHidden = null;
		this.isParent = null;
		this.name = null;
		this.noCheck = null;
		this.open = null;
		this.target = null;
		this.url = null;
	}

	private String pId;
	private String checked;
	private String chkDisabled;
	private String click;
	private String halfCheck;
	private String icon;
	private String iconClose;
	private String iconOpen;
	private String iconSkin;
	private String isHidden;
	private String isParent;
	private String name;
	private String noCheck;
	private String open;
	private String target;
	private String url;

	public void setParentId(String parentId) {
		this.pId = parentId;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public void setChkDisabled(String chkDisabled) {
		this.chkDisabled = chkDisabled;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public void setHalfCheck(String halfCheck) {
		this.halfCheck = halfCheck;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setIconClose(String iconClose) {
		this.iconClose = iconClose;
	}

	public void setIconOpen(String iconOpen) {
		this.iconOpen = iconOpen;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}

	public void setIsHidden(String isHidden) {
		this.isHidden = isHidden;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNoCheck(String noCheck) {
		this.noCheck = noCheck;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
