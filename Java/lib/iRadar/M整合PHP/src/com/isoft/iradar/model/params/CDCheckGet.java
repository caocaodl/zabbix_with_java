package com.isoft.iradar.model.params;

public class CDCheckGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] dcheckIds;
	private Long[] druleIds;
	private Long[] dhostIds;
	private Long[] dserviceIds;
	private Object selectDRules;
	private Object selectDHosts;
	private Object selectHosts;

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

	public Object getSelectDHosts() {
		return selectDHosts;
	}

	public void setSelectDHosts(Object selectDHosts) {
		this.selectDHosts = selectDHosts;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

}
