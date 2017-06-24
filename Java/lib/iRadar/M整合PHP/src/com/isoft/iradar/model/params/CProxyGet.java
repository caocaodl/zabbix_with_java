package com.isoft.iradar.model.params;

public class CProxyGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] proxyIds;
	private Object selectHosts;
	private Object selectInterface;
	private Object selectInterfaces;

	public Long[] getProxyIds() {
		return proxyIds;
	}

	public void setProxyIds(Long... proxyIds) {
		this.proxyIds = proxyIds;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

	public Object getSelectInterface() {
		return selectInterface;
	}

	public void setSelectInterface(Object selectInterface) {
		this.selectInterface = selectInterface;
	}

	public Object getSelectInterfaces() {
		return selectInterfaces;
	}

	public void setSelectInterfaces(Object selectInterfaces) {
		this.selectInterfaces = selectInterfaces;
	}
}
