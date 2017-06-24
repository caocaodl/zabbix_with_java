package com.isoft.imon.topo.engine.discover.bag.host;

import java.util.List;

import com.isoft.imon.topo.engine.discover.bag.SimpleBag;

/**
 * 域描述符数据块表格信息实体
 * @author ldd
 * 2014-2-21
 */
public final class FdbTableEntry extends SimpleBag {
	private static final long serialVersionUID = 201307091802004L;
    //设备ID
	private int elementId;
    //MAC地址
	private String mac;
    //端口
	private String port;
    //状态
	private int status;

	/**
	 *获取端口
	 * @return
	 * String
	 */
	public String getPort() {
		return this.port;
	}

	/**
	 *设置端口
	 * @param port
	 * void
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 *获取Mac
	 * @return
	 * String
	 */
	public String getMac() {
		return this.mac;
	}

	/**
	 *设置Mac
	 * @param mac
	 * void
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 *获取状态
	 * @return
	 * int
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 *设置状态
	 * @param status
	 * void
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 *获取元素ID
	 * @return
	 * int
	 */
	public int getElementId() {
		return this.elementId;
	}

	/**
	 *设置元素ID
	 * @param elementId
	 * void
	 */
	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	/* (non-Javadoc)
	 * 获取实体信息，即Mac
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.mac;
	}

	/* (non-Javadoc)
	 * @see com.isoft.engine.discover.bag.SimpleBag#persist(int, java.lang.String, java.util.List)
	 */
	public void persist(int elementId, String logTime, List<String> sqls) {
	}
}
