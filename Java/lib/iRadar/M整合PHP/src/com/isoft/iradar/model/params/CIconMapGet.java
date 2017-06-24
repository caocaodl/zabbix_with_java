package com.isoft.iradar.model.params;

public class CIconMapGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] iconmapIds;
	private Long[] sysmapIds;
	private Object selectMappings;

	public Long[] getIconmapIds() {
		return iconmapIds;
	}

	public void setIconmapIds(Long... iconmapIds) {
		this.iconmapIds = iconmapIds;
	}

	public Long[] getSysmapIds() {
		return sysmapIds;
	}

	public void setSysmapIds(Long... sysmapIds) {
		this.sysmapIds = sysmapIds;
	}

	public Object getSelectMappings() {
		return selectMappings;
	}

	public void setSelectMappings(Object selectMappings) {
		this.selectMappings = selectMappings;
	}

}
