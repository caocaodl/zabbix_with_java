package com.isoft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNodeItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String pId;
	private Boolean checked;
	private Boolean chkDisabled;
	private String click;
	private Boolean halfCheck;
	private String icon;
	private String iconClose;
	private String iconOpen;
	private String iconSkin;
	private Boolean isHidden;
	private Boolean isParent;
	private String name;
	private Boolean noCheck;
	private Boolean open;
	private String target;
	private String url;
	private String beforeClick;
	private List<TreeNodeItem> children;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public Boolean getChkDisabled() {
		return chkDisabled;
	}

	public void setChkDisabled(Boolean chkDisabled) {
		this.chkDisabled = chkDisabled;
	}

	public String getClick() {
		return click;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public Boolean getHalfCheck() {
		return halfCheck;
	}

	public void setHalfCheck(Boolean halfCheck) {
		this.halfCheck = halfCheck;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconClose() {
		return iconClose;
	}

	public void setIconClose(String iconClose) {
		this.iconClose = iconClose;
	}

	public String getIconOpen() {
		return iconOpen;
	}

	public void setIconOpen(String iconOpen) {
		this.iconOpen = iconOpen;
	}

	public String getIconSkin() {
		return iconSkin;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}

	public Boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getNoCheck() {
		return noCheck;
	}

	public void setNoCheck(Boolean noCheck) {
		this.noCheck = noCheck;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<TreeNodeItem> getChildren() {
		return children;
	}

	public void addChildren(List<TreeNodeItem> children) {
		if (children != null && !children.isEmpty()) {
			if (this.children == null) {
				this.children = new ArrayList<TreeNodeItem>(0);
			}
			this.children.addAll(children);
		}
	}

	public void setBeforeClick(String beforeClick) {
		this.beforeClick = beforeClick;
	}

	public String getBeforeClick() {
		return beforeClick;
	}

}
