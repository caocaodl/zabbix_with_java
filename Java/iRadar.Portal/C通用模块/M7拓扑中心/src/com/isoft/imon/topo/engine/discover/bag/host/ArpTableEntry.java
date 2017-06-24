package com.isoft.imon.topo.engine.discover.bag.host;

import java.util.List;

import com.isoft.imon.topo.admin.factory.DictionaryFactory;
import com.isoft.imon.topo.engine.discover.bag.SimpleBag;
import com.isoft.imon.topo.platform.policy.AnalysableAnnotation;

/**
 * 地址解析协议数据表
 * 
 * @author ldd 2014-2-19
 */
public final class ArpTableEntry extends SimpleBag {
	private static final long serialVersionUID = 201307091802001L;
	// 接口索引
	private String ifIndex;
	// IP地址
	private String ipAddress;
	// MAC地址
	private String mac;
	// 设备类型标识
	private int type;

	/**
	 * 获取接口索引
	 * 
	 * @return String
	 */
	public String getIfIndex() {
		return this.ifIndex;
	}

	/**
	 * 设置接口索引
	 * 
	 * @param ifIndex
	 *            void
	 */
	public void setIfIndex(String ifIndex) {
		this.ifIndex = ifIndex;
	}

	/**
	 * 获取IP地址
	 * 
	 * @return String
	 */
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 * 设置IP地址
	 * 
	 * @param ipAddress
	 *            void
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * 获取MAC字符串
	 * 
	 * @return String
	 */
	public String getMac() {
		return this.mac;
	}

	/**
	 * 设置Mac字符串
	 * 
	 * @param mac
	 *            void
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * 获取设备类型
	 * 
	 * @return int
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * 设置设备类型
	 * 
	 * @param type
	 *            void
	 */
	public void setType(int type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc) 获取实体字符串，由接口索引和ip地址拼接而成
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.ifIndex + "-" + this.ipAddress;
	}

	/**
	 * 获取类型名称
	 * 
	 * @return String
	 */
	public String getTypeName() {
		return DictionaryFactory.getFactory().getEntryValue("ipNetToMediaType", this.type);
	}
}
