package com.isoft.imon.topo.engine.discover.bag.host;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.platform.policy.AnalysableAnnotation;

/**
 * 设备接口表
 * 
 * @author Administrator
 * 
 * @date 2014年8月6日
 */
@AnalysableAnnotation(label = "设备接口表")
public final class IfTable extends CompositeBag<IfTableEntry> {
	private static final long serialVersionUID = 20130709152210L;

	/**
	 * 构造方法，初始化接口名称
	 */
	public IfTable() {
		this.name = IfTableEntry.class.getName();
	}

	/**
	 * 根据IP获取接口实体信息
	 * 
	 * @param ip
	 * @return
	 */
	public IfTableEntry getIfByIP(String ip) {
		if (ip != null) {
			for (IfTableEntry ife : this.entities.values())
				if (ife.isMyIpAddress(ip))
					return ife;
		}
		return null;
	}

	/**
	 * 根据索引获取接口实体信息
	 * 
	 * @param index
	 * @return
	 */
	public IfTableEntry getIfByIndex(String index) {
		if (index != null) {
			for (IfTableEntry ife : this.entities.values()) {
				if (index.equals(ife.getIndex())) {
					return ife;
				}
			}
		}
		return null;
	}

	/**
	 * 根据描述获取接口实体信息
	 * 
	 * @param descr
	 * @return
	 */
	public IfTableEntry getIfByDescr(String descr) {
		if (descr != null) {
			for (IfTableEntry ife : this.entities.values())
				if (descr.equals(ife.getDescr()))
					return ife;
		}
		return null;
	}

	/**
	 * 根据Mac获取接口实体信息
	 * 
	 * @param mac
	 * @return
	 */
	public IfTableEntry getIfByMac(String mac) {
		if (mac != null) {
			for (IfTableEntry ife : this.entities.values())
				if (mac.equalsIgnoreCase(ife.getMac()))
					return ife;
		}
		return null;
	}

	/**
	 * 包含Vlan,有则返回true，无则返回false
	 * 
	 * @return
	 */
	public boolean containsVlan() {
		for (IfTableEntry ife : this.entities.values()) {
			if (ife.getDescr().startsWith("Vlan"))
				return true;
		}
		return false;
	}
}
