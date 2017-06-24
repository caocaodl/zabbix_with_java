package com.isoft.imon.topo.platform.context;

import com.isoft.imon.topo.engine.discover.NetElement;

/**
 * 协议适配器抽象类
 * 
 * @author Administrator
 * 
 * @param <NE>
 * @date 2014年8月4日
 */
public abstract class ProtocolAdapter<NE extends NetElement> {
	protected Object connector;
	protected int responseTime;

	/**
	 * 获取响应时间
	 * @return
	 */
	public int getResponseTime() {
		return this.responseTime;
	}

	public Object getConnector() {
		return this.connector;
	}

	public boolean isUsable() {
		return this.connector != null;
	}

	public abstract void createConnector(NE paramNE);

	public abstract void freeConnector();
}
