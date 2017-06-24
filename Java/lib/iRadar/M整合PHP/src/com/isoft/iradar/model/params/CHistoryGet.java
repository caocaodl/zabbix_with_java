package com.isoft.iradar.model.params;

import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;

public class CHistoryGet extends CParamGet {
	
	private static final long serialVersionUID = 1L;

	private Integer history = ITEM_VALUE_TYPE_UINT64;
	private Long[] hostIds;
	private Long[] itemIds;
	private Long[] triggerIds;
	private Object groupOutput;

	public Integer getHistory() {
		return history;
	}

	public void setHistory(Integer history) {
		this.history = history;
	}

	public Long[] getHostIds() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

	public Long[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(Long... itemIds) {
		this.itemIds = itemIds;
	}

	public Long[] getTriggerIds() {
		return triggerIds;
	}

	public void setTriggerIds(Long... triggerIds) {
		this.triggerIds = triggerIds;
	}

	public Object getGroupOutput() {
		return groupOutput;
	}

	public void setGroupOutput(Object groupOutput) {
		this.groupOutput = groupOutput;
	}

}
