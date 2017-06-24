package com.isoft.imon.topo.engine.discover.element;

import java.util.HashSet;
import java.util.Set;

import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.Sniffer;
import com.isoft.imon.topo.engine.discover.sniffer.SnmpHostSniffer;

/**
 * 广播域
 * 
 * @author Administrator
 * 
 * @date 2014年8月7日
 */
public class BcastDomain extends NetElement {
	private Set<String> netAddresses;
	private int hostId;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BcastDomain(int hostId) {
		this.netAddresses = new HashSet();
		this.category = "BroadcastDomain";
		this.hostId = hostId;
		this.id = -1;
	}

	/**
	 * 添加网络地址
	 * 
	 * @param netAddress
	 */
	public void addAddress(String netAddress) {
		this.netAddresses.add(netAddress);
	}

	/**
	 * 判断是否包含网络地址
	 * 
	 * @param netAddress
	 * @return
	 */
	public boolean contain(String netAddress) {
		return this.netAddresses.contains(netAddress);
	}

	/**
	 * 判断是否包含网络地址
	 * 
	 * @param netAddress1
	 * @param netAddress2
	 * @return
	 */
	public boolean contain(String netAddress1, String netAddress2) {
		return (this.netAddresses.contains(netAddress1))
				&& (this.netAddresses.contains(netAddress2));
	}

	/**
	 * 判断网络地址是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return this.netAddresses.isEmpty();
	}

	/**
	 * 获取网络地址
	 * 
	 * @return
	 */
	public Set<String> getNetAddresses() {
		return this.netAddresses;
	}

	/*
	 * 转换为字符串 (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer info = new StringBuffer(100);
		info.append(this.id).append(".");
		info.append("BroadcastDomain:").append("\n");
		for (String na : this.netAddresses) {
			info.append(na).append("\n");
		}
		return info.toString();
	}

	/**
	 * 设置主机ID
	 * 
	 * @param hostId
	 */
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	/**
	 * 获取主机ID
	 * 
	 * @return
	 */
	public int getHostId() {
		return this.hostId;
	}

	/*
	 * 获取嗅探器类 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.NetElement#getSnifferClazz()
	 */
	public Class<? extends Sniffer> getSnifferClazz() {
		return SnmpHostSniffer.class;
	}
}
