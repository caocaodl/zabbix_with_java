package com.isoft.imon.topo.engine.discover.bag.host.cisco;

import java.util.List;

import com.isoft.imon.topo.engine.discover.bag.SimpleBag;

/**
 * 思科发现协议
 * 
 * @author ldd 2014-2-19
 */
public final class CdpTableEntry extends SimpleBag {
	private static final long serialVersionUID = 20130709151811L;
	// 远程IP地址
	private String remoteIpAddr;
	// 远程接口描述
	private String remoteIfDescr;

	/**
	 * 
	 * @return String
	 */
	public String getRemoteIfDescr() {
		return this.remoteIfDescr;
	}

	/**
	 * 
	 * @param remoteIfDescr
	 *            void
	 */
	public void setRemoteIfDescr(String remoteIfDescr) {
		this.remoteIfDescr = remoteIfDescr;
	}

	/**
	 * 
	 * @return String
	 */
	public String getRemoteIpAddr() {
		return this.remoteIpAddr;
	}

	/**
	 * 
	 * @param remoteIpAddr
	 *            void
	 */
	public void setRemoteIpAddr(String remoteIpAddr) {
		this.remoteIpAddr = remoteIpAddr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.remoteIpAddr + "-" + this.remoteIfDescr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#persist(int,
	 * java.lang.String, java.util.List)
	 */
	public void persist(int elementId, String logTime, List<String> sqls) {
	}
}
