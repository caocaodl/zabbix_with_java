package com.isoft.iradar.model.params;

public class CDServiceGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] dserviceIds;
	private Long[] dhostIds;
	private Long[] dcheckIds;
	private Long[] druleIds;
	private Object selectDRules;
	private Object selectDHosts;
	private Object selectDChecks;
	private Object selectHosts;

	public Long[] getDserviceIds() {
		return dserviceIds;
	}

	public void setDserviceIds(Long... dserviceIds) {
		this.dserviceIds = dserviceIds;
	}

	public Long[] getDhostIds() {
		return dhostIds;
	}

	public void setDhostIds(Long... dhostIds) {
		this.dhostIds = dhostIds;
	}

	public Long[] getDcheckIds() {
		return dcheckIds;
	}

	public void setDcheckIds(Long... dcheckIds) {
		this.dcheckIds = dcheckIds;
	}

	public Long[] getDruleIds() {
		return druleIds;
	}

	public void setDruleIds(Long... druleIds) {
		this.druleIds = druleIds;
	}

	public Object getSelectDRules() {
		return selectDRules;
	}

	public void setSelectDRules(Object selectDRules) {
		this.selectDRules = selectDRules;
	}

	public Object getSelectDHosts() {
		return selectDHosts;
	}

	public void setSelectDHosts(Object selectDHosts) {
		this.selectDHosts = selectDHosts;
	}

	public Object getSelectDChecks() {
		return selectDChecks;
	}

	public void setSelectDChecks(Object selectDChecks) {
		this.selectDChecks = selectDChecks;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

}
