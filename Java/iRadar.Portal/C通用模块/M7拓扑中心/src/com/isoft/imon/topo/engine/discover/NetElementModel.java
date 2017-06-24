package com.isoft.imon.topo.engine.discover;

/**
 * 网络元素模型
 * 
 * @author ldd 2014-2-19
 */
public final class NetElementModel {
	// 厂商
	private String enterprise;
	// 对象表示符
	private String oid;
	// 型号
	private String model;
	// 象征符号
	private String symbol;
	// 类型
	private String category;
	// 系统描述
	private String sysDescr;

	/**
	 * 获取厂商
	 * 
	 * @return
	 */
	public String getEnterprise() {
		return this.enterprise;
	}

	/**
	 * 设置厂商
	 * 
	 * @param enterprise
	 */
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	/**
	 * 获取模型
	 * 
	 * @return
	 */
	public String getModel() {
		return this.model;
	}

	/**
	 * 设置模型
	 * 
	 * @param model
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * 获取类别
	 * 
	 * @return
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * 设置类别
	 * 
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * 获取OID
	 * 
	 * @return
	 */
	public String getOid() {
		return this.oid;
	}

	/**
	 * 设置OID
	 * 
	 * @param oid
	 */
	public void setOid(String oid) {
		this.oid = oid;
	}

	/**
	 * 获取标识符
	 * 
	 * @return
	 */
	public String getSymbol() {
		return this.symbol;
	}

	/**
	 * 设置标识符
	 * 
	 * @param symbol
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * 获取系统描述
	 * 
	 * @return
	 */
	public String getSysDescr() {
		return this.sysDescr;
	}

	/**
	 * 设置系统描述
	 * 
	 * @param sysDescr
	 */
	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}
}
