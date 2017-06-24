package com.isoft.imon.topo.engine.discover.element;

import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.Sniffer;
import com.isoft.imon.topo.engine.discover.sniffer.SnmpHostSniffer;
import com.isoft.imon.topo.host.util.NetworkUtil;

/**
 * 子网类
 * 
 * @author Administrator
 * 
 * @date 2014年8月7日
 */
public class Subnet extends NetElement {

	private static final String VLAN = "Vlan";
	private static final String VLAN_INTERFACE = "Vlan-interface";
	private String netAddress;
	private String netMask;
	private String gateway;
	private boolean vlan;
	private long startIp;
	private long endIp;

	public Subnet() {
		this.symbol = "subnet";
		this.category = "Subnet";
	}

	public Subnet(String netAddress, String netMask) {
		this.netAddress = netAddress;
		this.netMask = netMask;
		this.symbol = "subnet";
		this.category = "Subnet";

		this.startIp = NetworkUtil.ipToLong(netAddress);
		this.endIp = (this.startIp + NetworkUtil.getSubnetIPTotal(netMask));
	}

	/**
	 * 获取网络地址
	 * 
	 * @return
	 */
	public String getNetAddress() {
		return this.netAddress;
	}

	/**
	 * 设置网络地址
	 * 
	 * @param netAddress
	 */
	public void setNetAddress(String netAddress) {
		this.netAddress = netAddress;
		if ((this.netAddress != null) && (this.netMask != null)) {
			this.startIp = NetworkUtil.ipToLong(netAddress);
			this.endIp = (this.startIp
					+ NetworkUtil.getSubnetIPTotal(this.netMask) + 1L);
		}
	}

	/**
	 * 获取子网掩码
	 * 
	 * @return
	 */
	public String getNetMask() {
		return this.netMask;
	}

	/**
	 * 设置子网掩码
	 * 
	 * @param netMask
	 */
	public void setNetMask(String netMask) {
		this.netMask = netMask;
		if ((this.netAddress != null) && (this.netMask != null)) {
			this.startIp = NetworkUtil.ipToLong(this.netAddress);
			this.endIp = (this.startIp + NetworkUtil.getSubnetIPTotal(netMask));
		}
	}

	/**
	 * 获取开始IP地址
	 * 
	 * @return
	 */
	public String getStartIp() {
		return NetworkUtil.longToIp(this.startIp);
	}

	/**
	 * 获取结束IP地址
	 * 
	 * @return
	 */
	public String getEndIp() {
		return NetworkUtil.longToIp(this.endIp);
	}

	/**
	 * 获取长整型开始IP地址
	 * 
	 * @return
	 */
	public long getStartLongIp() {
		return this.startIp;
	}

	/**
	 * 获取长整形结束IP地址
	 * 
	 * @return
	 */
	public long getEndLongIp() {
		return this.endIp;
	}

	/**
	 * 是否为虚拟局域网
	 * 
	 * @return
	 */
	public boolean isVlan() {
		return this.vlan;
	}

	/**
	 * 设置是否为虚拟局域网
	 * 
	 * @param vlan
	 */
	public void setVlan(boolean vlan) {
		this.vlan = vlan;
	}

	/**
	 * 获取虚拟局域网ID
	 * 
	 * @return
	 */
	public String getVlanId() {
		if (this.alias.startsWith(VLAN_INTERFACE))
			return this.alias.substring(VLAN_INTERFACE.length());
		if (this.alias.startsWith(VLAN))
			return this.alias.substring(VLAN.length());
		return "-1";
	}

	/**
	 * 获取网关
	 * 
	 * @return
	 */
	public String getGateway() {
		return this.gateway;
	}

	/**
	 * 设置网关
	 * 
	 * @param gateway
	 */
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	/*
	 * 获取IP地址 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.NetElement#getIpAddress()
	 */
	public String getIpAddress() {
		return this.netAddress;
	}

	/*
	 * 判断是否等于 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.NetElement#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof Subnet)))
			return false;
		Subnet that = (Subnet) obj;

		return (getNetAddress().equals(that.getNetAddress()))
				&& (getNetMask().equals(that.getNetMask()));
	}

	/*
	 * 哈希码 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.NetElement#hashCode()
	 */
	public int hashCode() {
		int result = 1;
		result = result * 31 + getNetAddress().hashCode();
		result = result * 31 + getNetMask().hashCode();
		return result;
	}

	/**
	 * 判断IP是否在范围内
	 * 
	 * @param ipAddress
	 * @return
	 */
	public boolean ipInScope(String ipAddress) {
		long _longip = NetworkUtil.ipToLong(ipAddress);
		return (_longip >= this.startIp) && (_longip <= this.endIp);
	}

	/*
	 * 转换为字符串 (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer info = new StringBuffer(100);
		info.append(this.id).append(".");
		info.append("子网:alias=");
		info.append(this.alias);
		info.append(",addr=");
		info.append(this.netAddress);
		info.append(",mask=");
		info.append(this.netMask);
		return info.toString();
	}

	/*
	 * 获取图片 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.NetElement#getImage()
	 */
	public String getImage() {
		return "subnet.gif";
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
