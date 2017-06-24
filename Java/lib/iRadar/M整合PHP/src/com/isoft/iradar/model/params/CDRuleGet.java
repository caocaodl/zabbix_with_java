package com.isoft.iradar.model.params;


public class CDRuleGet extends CParamGet {
	
	private static final long serialVersionUID = 1L;
	
	private Long[] druleIds;
	private Long[] dhostIds;
	private Long[] dserviceIds;
	private Long[] dcheckIds;
	private Object selectDHosts;
	private Object selectDServices;
	private Object selectDChecks;

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

	public Long[] getDcheckids() {
		return dcheckIds;
	}

	public void setDcheckIds(Long... dcheckIds) {
		this.dcheckIds = dcheckIds;
	}

	public Object getSelectDHosts() {
		return selectDHosts;
	}

	public void setSelectDHosts(Object selectDHosts) {
		this.selectDHosts = selectDHosts;
	}

	public Object getSelectDServices() {
		return selectDServices;
	}

	public void setSelectDServices(Object selectDServices) {
		this.selectDServices = selectDServices;
	}

	public Object getSelectDChecks() {
		return selectDChecks;
	}

	public void setSelectDChecks(Object selectDChecks) {
		this.selectDChecks = selectDChecks;
	}

}
