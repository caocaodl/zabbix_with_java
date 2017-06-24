package com.isoft.imon.topo.engine.discover.bag.host;

import java.util.List;

import com.isoft.imon.topo.engine.discover.bag.SimpleBag;

/**
 * IP数据表格实体
 * @author ldd
 * 2014-2-22
 */
public final class IPTableEntry extends SimpleBag {
	private static final long serialVersionUID = 201307091802005L;
	//IP地址
	private String ipAddress;
	//接口索引
	private String ifIndex;
	//子网掩码
	private String mask;

	/**
	 *获取IP地址
	 * @return
	 * String
	 */
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 *设置IP地址
	 * @param ipAddress
	 * void
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 *获取接口索引
	 * @return
	 * String
	 */
	public String getIfIndex() {
		return this.ifIndex;
	}

	/**
	 *设置接口索引
	 * @param ifIndex
	 * void
	 */
	public void setIfIndex(String ifIndex) {
		this.ifIndex = ifIndex;
	}

	/**
	 *
	 * @return
	 * String
	 */
	public String getMask() {
		return this.mask;
	}

	/**
	 *
	 * @param mask
	 * void
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	/* (non-Javadoc)获取实体信息
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.ipAddress;
	}

	/* (non-Javadoc)
	 * @see com.isoft.engine.discover.bag.SimpleBag#persist(int, java.lang.String, java.util.List)
	 */
	public void persist(int elementId, String logTime, List<String> sqls) {
	}
}
