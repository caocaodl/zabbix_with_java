package com.isoft.imon.topo.engine.discover.bag.host;

import java.util.List;

import com.isoft.imon.topo.engine.discover.bag.SimpleBag;

/**
 * 端口表格数据实体
 * @author ldd
 * 2014-2-22
 */
public final class PortTableEntry extends SimpleBag {
	private static final long serialVersionUID = 201307091806006L;
     //端口
	private String port;
    //接口索引
	private String ifIndex;

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

	/* (non-Javadoc)
	 * 获取实体，由端口和接口索引拼接而成
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.port + "-" + this.ifIndex;
	}

	/* (non-Javadoc)
	 * @see com.isoft.engine.discover.bag.SimpleBag#persist(int, java.lang.String, java.util.List)
	 */
	public void persist(int elementId, String logTime, List<String> sqls) {
	}
}
