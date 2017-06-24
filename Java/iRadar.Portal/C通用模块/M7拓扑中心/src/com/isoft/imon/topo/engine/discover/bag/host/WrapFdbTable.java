package com.isoft.imon.topo.engine.discover.bag.host;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**包装Fdb表
 * @author Administrator
 * @date 2014年8月6日 
 */
public final class WrapFdbTable implements Serializable {
	private static final long serialVersionUID = 201307091808020L;
	//端口映射
	private Map<String, Set<String>> portMap;
	//端口
	private List<String> ports;

	/**
	 * 构造方法
	 * @param fdbTable
	 */
	public WrapFdbTable(FdbTable fdbTable) {
		this.portMap = new HashMap<String, Set<String>>();
		for (FdbTableEntry entry : fdbTable.getEntities()) {
			if (this.portMap.containsKey(entry.getPort())) {
				this.portMap.get(entry.getPort()).add(entry.getMac());
			} else {
				Set<String> macs = new HashSet<String>();
				macs.add(entry.getMac());
				this.portMap.put(entry.getPort(), macs);
			}
		}
		this.ports = new ArrayList<String>(this.portMap.keySet());
	}

	/**
	 * 获取端口
	 * @return
	 */
	public List<String> getPorts() {
		return this.ports;
	}

	/**
	 * 根据端口获取Mac
	 * @param port
	 * @return
	 */
	public Set<String> getMacsByPort(String port) {
		if ((port != null) && (this.portMap.containsKey(port)))
			return this.portMap.get(port);
		return null;
	}
}
