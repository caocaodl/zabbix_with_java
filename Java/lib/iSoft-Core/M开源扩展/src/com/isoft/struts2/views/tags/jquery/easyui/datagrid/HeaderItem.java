package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import java.util.ArrayList;
import java.util.List;

public class HeaderItem {
	protected List<ColumnItem> columnList = new ArrayList<ColumnItem>();

	protected void addColumnItem(ColumnItem item) {
		columnList.add(item);
	}

	private Boolean frozen;

	public Boolean getFrozen() {
		return frozen;
	}

	public void setFrozen(Boolean frozen) {
		this.frozen = frozen;
	}

}
