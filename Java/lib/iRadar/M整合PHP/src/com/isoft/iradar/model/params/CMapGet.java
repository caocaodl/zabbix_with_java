package com.isoft.iradar.model.params;

public class CMapGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] sysmapIds;
	private Object selectSelements;
	private Object selectLinks;
	private Object selectIconMap;
	private Object selectUrls;
	private Object expandUrls;

	public Long[] getSysmapIds() {
		return sysmapIds;
	}

	public void setSysmapIds(Long... sysmapIds) {
		this.sysmapIds = sysmapIds;
	}

	public Object getSelectSelements() {
		return selectSelements;
	}

	public void setSelectSelements(Object selectSelements) {
		this.selectSelements = selectSelements;
	}

	public Object getSelectLinks() {
		return selectLinks;
	}

	public void setSelectLinks(Object selectLinks) {
		this.selectLinks = selectLinks;
	}

	public Object getSelectIconMap() {
		return selectIconMap;
	}

	public void setSelectIconMap(Object selectIconMap) {
		this.selectIconMap = selectIconMap;
	}

	public Object getSelectUrls() {
		return selectUrls;
	}

	public void setSelectUrls(Object selectUrls) {
		this.selectUrls = selectUrls;
	}

	public Object getExpandUrls() {
		return expandUrls;
	}

	public void setExpandUrls(Object expandUrls) {
		this.expandUrls = expandUrls;
	}

}
