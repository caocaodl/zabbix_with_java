package com.isoft.imon.topo.platform.context;

import com.isoft.imon.topo.engine.discover.NetElement;

/**
 * 轮询池类
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public class PollingPool extends TopoPool {
	private static PollingPool pool = new PollingPool();

	public static PollingPool getPool() {
		return pool;
	}

	/**
	 * 根据网元IP获取网元
	 * 
	 * @param ip
	 * @param category
	 * @return
	 */
	public NetElement getElementByIP(String ip, String category) {
		for (NetElement ne : this.elements) {
			if ((ne.getIpAddress().equals(ip)) && (ne.getCategory().equals(category)))
				return ne;
		}
		return null;
	}
}
