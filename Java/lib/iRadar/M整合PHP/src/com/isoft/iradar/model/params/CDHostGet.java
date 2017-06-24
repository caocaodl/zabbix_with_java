package com.isoft.iradar.model.params;

public class CDHostGet extends CParamGet {
	
	private static final long serialVersionUID = 1L;
	
	private Long[] druleIds;
	private Long[] dhostIds;
	private Long[] dserviceIds;
	private Object selectDRules;
	private Object selectDServices;

	public Long[] getDruleIds() {
		return druleIds;
	}

	public void setDruleIds(Long... druleIds) {
		this.druleIds = druleIds;
	}

	public Long[] getDhostIds() {
		return dhostIds;
	}

	public void setDhostIds(Long... dhostIds) {
		this.dhostIds = dhostIds;
	}

	public Long[] getDserviceIds() {
		return dserviceIds;
	}

	public void setDserviceIds(Long... dserviceIds) {
		this.dserviceIds = dserviceIds;
	}

	public Object getSelectDRules() {
		return selectDRules;
	}

	public void setSelectDRules(Object selectDRules) {
		this.selectDRules = selectDRules;
	}

	public Object getSelectDServices() {
		return selectDServices;
	}

	public void setSelectDServices(Object selectDServices) {
		this.selectDServices = selectDServices;
	}

}
