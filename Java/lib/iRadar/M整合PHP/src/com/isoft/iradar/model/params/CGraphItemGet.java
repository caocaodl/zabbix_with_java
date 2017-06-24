package com.isoft.iradar.model.params;

public class CGraphItemGet extends CGraphGeneralGet {
	
	private static final long serialVersionUID = 1L;
	
	private Long[] graphIds;
	private Long[] itemIds;
	private String type;
	private Boolean expandData;
	private Object selectGraphs;

	public Long[] getGraphIds() {
		return graphIds;
	}

	public void setGraphIds(Long... graphIds) {
		this.graphIds = graphIds;
	}

	public Long[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(Long... itemIds) {
		this.itemIds = itemIds;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getExpandData() {
		return expandData;
	}

	public void setExpandData(Boolean expandData) {
		this.expandData = expandData;
	}

	public Object getSelectGraphs() {
		return selectGraphs;
	}

	public void setSelectGraphs(Object selectGraphs) {
		this.selectGraphs = selectGraphs;
	}
}