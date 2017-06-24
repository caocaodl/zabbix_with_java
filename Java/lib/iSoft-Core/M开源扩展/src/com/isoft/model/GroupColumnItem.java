package com.isoft.model;

import java.io.Serializable;

public class GroupColumnItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String startColumnName;
	private int numberOfColumns;
	private String titleText;

	public GroupColumnItem(String startColumnName, int numberOfColumns,
			String titleText) {
		this.startColumnName = startColumnName;
		this.numberOfColumns = numberOfColumns;
		this.titleText = titleText;
	}

	public String getStartColumnName() {
		return startColumnName;
	}

	public void setStartColumnName(String startColumnName) {
		this.startColumnName = startColumnName;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

}
