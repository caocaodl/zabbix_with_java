package com.isoft.iradar.model.params;

public class CGraphGeneralGet extends CParamGet {
	
	private static final long serialVersionUID = 1L;
	
	private Object selectGroups;
	private Object selectTemplates;
	private Object selectHosts;
	private Object selectGraphItems;

	public Object getSelectGroups() {
		return selectGroups;
	}

	public void setSelectGroups(Object selectGroups) {
		this.selectGroups = selectGroups;
	}

	public Object getSelectTemplates() {
		return selectTemplates;
	}

	public void setSelectTemplates(Object selectTemplates) {
		this.selectTemplates = selectTemplates;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

	public Object getSelectGraphItems() {
		return selectGraphItems;
	}

	public void setSelectGraphItems(Object selectGraphItems) {
		this.selectGraphItems = selectGraphItems;
	}

}