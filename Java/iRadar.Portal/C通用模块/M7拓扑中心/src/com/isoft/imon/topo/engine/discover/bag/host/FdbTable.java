package com.isoft.imon.topo.engine.discover.bag.host;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;

/**
 * FDB地址表
 * 
 * @author Administrator
 * 
 * @date 2014年8月6日
 */
public final class FdbTable extends CompositeBag<FdbTableEntry> {
	private static final long serialVersionUID = 201307091802003L;
	private WrapFdbTable wrapFdbTable;

	/**
	 * 构造方法，初始化名称
	 */
	public FdbTable() {
		this.name = FdbTableEntry.class.getName();
	}

	/**
	 * 通过Mac获取端口信息
	 * 
	 * @param mac
	 * @return
	 */
	public String getPortByMac(String mac) {
		if (mac == null)
			return null;

		for (FdbTableEntry entry : getEntities()) {
			if (mac.equals(entry.getMac()))
				return entry.getPort();
		}
		return null;
	}

	/**
	 * 获取Fdb包装地址表
	 * 
	 * @return
	 */
	public WrapFdbTable getWrapFdbTable() {
		if (this.wrapFdbTable == null)
			this.wrapFdbTable = new WrapFdbTable(this);
		return this.wrapFdbTable;
	}
}
