package com.isoft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FuncItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String pid;
	private String funcName;
	private String funcUrl;
	private Boolean entrance;
	private String renderType;
	private String renderStyle;
	private String renderUrl;
	private String iconClass;
	private Boolean leaf;
	private String role;

	private FuncItem parentFunc;
	private List<FuncItem> subFuncList = new ArrayList<FuncItem>(5);

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getFuncUrl() {
		return funcUrl;
	}

	public void setFuncUrl(String funcUrl) {
		this.funcUrl = funcUrl;
	}

	public Boolean isEntrance() {
		return entrance;
	}

	public void setEntrance(Boolean entrance) {
		this.entrance = entrance;
	}

	public String getRenderType() {
		if (renderType == null || renderType.length() == 0) {
			return "tree";
		}
		return renderType;
	}

	public void setRenderType(String renderType) {
		this.renderType = renderType;
	}

	public String getRenderStyle() {
		if (renderStyle == null) {
			return "";
		}
		return renderStyle;
	}

	public void setRenderStyle(String renderStyle) {
		this.renderStyle = renderStyle;
	}

	public String getRenderUrl() {
		return renderUrl;
	}

	public void setRenderUrl(String renderUrl) {
		this.renderUrl = renderUrl;
	}

	public String getIconClass() {
		if (iconClass == null || iconClass.length() == 0) {
			return "icon";
		}
		return iconClass;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

	public Boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public FuncItem getParentFunc() {
		return parentFunc;
	}

	public void setParentFunc(FuncItem parentFunc) {
		this.parentFunc = parentFunc;
		if (this.parentFunc != null) {
			this.parentFunc.addSubFunc(this);
		}
	}

	public List<FuncItem> getSubFuncList() {
		return subFuncList;
	}

	public void addSubFunc(FuncItem funcItem) {
		subFuncList.add(funcItem);
	}

	public boolean isMultiLevelSubs() {
		return "-1".equals(pid) && !isLeaf() && subFuncList.size() > 0
				&& !subFuncList.get(0).isLeaf();
	}

}
