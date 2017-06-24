package com.isoft.struts2.views.tags.ui.ztree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.model.TreeNodeItem;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.TreeNodeHolder;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $ZTreeNode extends AndurilUIComponent implements TreeNodeHolder {

	public $ZTreeNode(ValueStack stack, HttpServletRequest request,
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
	public boolean usesBody() {
		return false;
	}

	@Override
	protected void popComponentStack() {
		super.popComponentStack();
		if(this.isRendered()){
			Object component = this.getComponentStack().peek();
			if (component instanceof TreeNodeHolder) {
				TreeNodeItem i = new TreeNodeItem();
				i.setId(getId());
				i.setpId(pId);
				i.setChecked(checked);
				i.setChkDisabled(chkDisabled);
				i.setClick(click);
				i.setHalfCheck(halfCheck);
				i.setIcon(icon);
				i.setIconClose(iconClose);
				i.setIconOpen(iconOpen);
				i.setIconSkin(iconSkin);
				i.setIsHidden(isHidden);
				i.setIsParent(isParent);
				i.setName(name);
				i.setNoCheck(noCheck);
				i.setOpen(open);
				i.setTarget(target);
				i.setUrl(url);
				i.addChildren(children);
				((TreeNodeHolder) component).pushTreeNodeItem(i);
				this.children = null;
			}
		}
	}

	private List<TreeNodeItem> children = null;

	@Override
	public void pushTreeNodeItem(TreeNodeItem treeNodeItem) {
		if (children == null) {
			children = new ArrayList<TreeNodeItem>(1);
		}
		children.add(treeNodeItem);
	}

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

}
