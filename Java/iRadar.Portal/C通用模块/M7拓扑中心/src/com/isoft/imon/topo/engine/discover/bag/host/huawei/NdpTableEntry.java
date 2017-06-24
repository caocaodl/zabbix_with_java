package com.isoft.imon.topo.engine.discover.bag.host.huawei;

import java.util.List;

import com.isoft.imon.topo.engine.discover.bag.SimpleBag;

/**
 * 华为发现协议
 * 
 * @author ldd 2014-2-22
 */
public final class NdpTableEntry extends SimpleBag {

	private static final long serialVersionUID = 1L;
	// 设备ID
	private String deviceId;
	// 端口名称
	private String portName;

	/**
	 * 获取设备ID
	 * 
	 * @return String
	 */
	public String getDeviceId() {
		return this.deviceId;
	}

	/**
	 * 设置设备ID
	 * 
	 * @param deviceId
	 *            void
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * 获取端口名称
	 * 
	 * @return String
	 */
	public String getPortName() {
		return this.portName;
	}

	/**
	 * 设置端口名称
	 * 
	 * @param portName
	 *            void
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}

	/*
	 * (non-Javadoc) 获取实体
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.deviceId + "-" + this.portName;
	}
}
