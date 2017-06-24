package com.isoft.imon.topo.engine.discover.element;

import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.Sniffer;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.sniffer.SnmpHostSniffer;
import com.isoft.imon.topo.host.util.HostConstants;

/**
 * 链路类
 * 
 * @author Administrator
 * 
 * @date 2014年8月7日
 */
public class Link extends NetElement {

	private int startId;
	private String startIfIndex;
	private String startIfDescr;
	private String startIp;
	private String startMac;
	private int endId;
	private String endIfIndex;
	private String endIfDescr;
	private String endIp;
	private String endMac;
	private int bandWidth;
	private String type;
	private String tag;
	private int trafficIf;
	private int trafficDirect;
	private int backup;

	public Link() {
		this.symbol = "genericLink";
		this.category = "Link";
		this.trafficIf = 1;
		this.trafficDirect = 1;
	}

	/**
	 * 获取带宽
	 * 
	 * @return
	 */
	public int getBandWidth() {
		return this.bandWidth;
	}

	/**
	 * 设置带宽
	 * 
	 * @param bandWidth
	 */
	public void setBandWidth(int bandWidth) {
		this.bandWidth = bandWidth;
	}

	/**
	 * 获取结束设备ID
	 * 
	 * @return
	 */
	public int getEndId() {
		return this.endId;
	}

	/**
	 * 设置结束设备ID
	 * 
	 * @param endId
	 */
	public void setEndId(int endId) {
		this.endId = endId;
	}

	/**
	 * 获取开始设备ID
	 * 
	 * @return
	 */
	public int getStartId() {
		return this.startId;
	}

	/**
	 * 设置开始设备ID
	 * 
	 * @param startId
	 */
	public void setStartId(int startId) {
		this.startId = startId;
	}

	/**
	 * 获取类型
	 * 
	 * @return
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取结束设备IP地址
	 * 
	 * @return
	 */
	public String getEndIp() {
		return this.endIp;
	}

	/**
	 * 设置结束设备IP地址
	 * 
	 * @param endIp
	 */
	public void setEndIp(String endIp) {
		if ((endIp != null) && (endIp.indexOf(",") >= 1))
			this.endIp = endIp.split(",")[0];
		else
			this.endIp = endIp;
	}

	/**
	 * 获取开始设备IP地址
	 * 
	 * @return
	 */
	public String getStartIp() {
		return this.startIp;
	}

	/**
	 * 设置开始设备IP地址
	 * 
	 * @param startIp
	 */
	public void setStartIp(String startIp) {
		if ((startIp != null) && (startIp.indexOf(",") >= 1))
			this.startIp = startIp.split(",")[0];
		else
			this.startIp = startIp;
	}

	/**
	 * 获取结束设备MAC地址
	 * 
	 * @return
	 */
	public String getEndMac() {
		return this.endMac;
	}

	/**
	 * 设置结束设备MAC地址
	 * 
	 * @param endMac
	 */
	public void setEndMac(String endMac) {
		this.endMac = endMac;
	}

	/**
	 * 获取开始设备MAC地址
	 * 
	 * @return
	 */
	public String getStartMac() {
		return this.startMac;
	}

	/**
	 * 设置开始设备MAC地址
	 * 
	 * @param startMac
	 */
	public void setStartMac(String startMac) {
		this.startMac = startMac;
	}

	/**
	 * 获取结束接口描述
	 * 
	 * @return
	 */
	public String getEndIfDescr() {
		return this.endIfDescr;
	}

	/**
	 * 设置结束接口描述
	 * 
	 * @param endIfDescr
	 */
	public void setEndIfDescr(String endIfDescr) {
		this.endIfDescr = endIfDescr;
	}

	/**
	 * 获取开始接口描述
	 * 
	 * @return
	 */
	public String getStartIfDescr() {
		return this.startIfDescr;
	}

	/**
	 * 设置开始接口描述
	 * 
	 * @param startIfDescr
	 */
	public void setStartIfDescr(String startIfDescr) {
		this.startIfDescr = startIfDescr;
	}

	/**
	 * 获取开始接口索引
	 * 
	 * @return
	 */
	public String getStartIfIndex() {
		return this.startIfIndex;
	}

	/**
	 * 设置开始接口索引
	 * 
	 * @param startIfIndex
	 */
	public void setStartIfIndex(String startIfIndex) {
		this.startIfIndex = startIfIndex;
	}

	/**
	 * 获取结束接口索引
	 * 
	 * @return
	 */
	public String getEndIfIndex() {
		return this.endIfIndex;
	}

	/**
	 * 设置结束接口索引
	 * 
	 * @param endIfIndex
	 */
	public void setEndIfIndex(String endIfIndex) {
		this.endIfIndex = endIfIndex;
	}

	/**
	 * 获取标记
	 * 
	 * @return
	 */
	public String getTag() {
		return this.tag;
	}

	/**
	 * 设置标记
	 * 
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * 获取备份
	 * 
	 * @return
	 */
	public int getBackup() {
		return this.backup;
	}

	/**
	 * 设置备份
	 * 
	 * @param backup
	 */
	public void setBackup(int backup) {
		this.backup = backup;
	}

	/**
	 * 获取传输接口
	 * 
	 * @return
	 */
	public int getTrafficIf() {
		return this.trafficIf;
	}

	/**
	 * 设置传输接口
	 * 
	 * @param trafficIf
	 */
	public void setTrafficIf(int trafficIf) {
		this.trafficIf = trafficIf;
	}

	/**
	 * 获取传输方向
	 * 
	 * @return
	 */
	public int getTrafficDirect() {
		return this.trafficDirect;
	}

	/**
	 * 设置传输方向
	 * 
	 * @param trafficDirect
	 */
	public void setTrafficDirect(int trafficDirect) {
		this.trafficDirect = trafficDirect;
	}

	/*
	 * 判断是否等于 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.NetElement#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof Link))) {
			return false;
		}
		Link that = (Link) obj;
//		if ((this.startId == that.startId) && (this.endId == that.endId)
//				&& (this.startIfIndex.equals(that.startIfIndex))
//				&& (this.endIfIndex.equals(that.endIfIndex))) {
//			return true;
//		}
		
		if ((this.startId == that.startId) && (this.endId == that.endId)
				&& (this.startMac.equals(that.startMac))
				&& (this.endMac.equals(that.endMac))) {
			return true;
		}

//		return (this.startId == that.endId) && (this.endId == that.startId)
//				&& (this.startIfIndex.equals(that.endIfIndex))
//				&& (this.endIfIndex.equals(that.startIfIndex));
		
		return (this.startId == that.endId) && (this.endId == that.startId)
				&& (this.startMac.equals(that.endMac))
				&& (this.endMac.equals(that.startMac));
	}

	/*
	 * 哈希编码 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.NetElement#hashCode()
	 */
	public int hashCode() {
		int result = this.startId*31 + this.endId;
		result = result * 31 + this.startIfIndex.hashCode()
				+ this.endIfIndex.hashCode();
		return result;
	}

	/*
	 * 转换为字符串 (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer linkStr = new StringBuffer(30);
		linkStr.append("\n").append(this.id).append(".");
		linkStr.append("链路:alias=").append(this.alias);
		linkStr.append(",[").append(this.startId).append("]-startIf=")
				.append(this.startIfIndex);
		linkStr.append(",[").append(this.endId).append("]-endIf=")
				.append(this.endIfIndex);
		linkStr.append(",tag=").append(this.tag);

		return linkStr.toString();
	}

	/**
	 * 创建链路
	 * 
	 * @param host1
	 * @param ife1
	 * @param host2
	 * @param ife2
	 * @return
	 */
	public static Link createLink(Host host1, IfTableEntry ife1, Host host2,
			IfTableEntry ife2) {
		return createLink(host1, ife1, host2, ife2, null);
	}

	/**
	 * 创建链路
	 * 
	 * @param host1
	 * @param ife1
	 * @param host2
	 * @param ife2
	 * @param alias
	 * @return
	 */
	public static Link createLink(Host host1, IfTableEntry ife1, Host host2,
			IfTableEntry ife2, String alias) {
		if (host1.getId() > host2.getId()) {
			Host _host = host1;
			IfTableEntry _ife = ife1;
			host1 = host2;
			ife1 = ife2;
			host2 = _host;
			ife2 = _ife;
		}

		Link link = new Link();
		link.setStartId(host1.getId());
		link.setStartIfIndex(ife1.getIndex());
		link.setStartIfDescr(ife1.getDescr());
		link.setStartIp(ife1.getIpAddress());
		link.setStartMac(ife1.getMac());

		link.setEndId(host2.getId());
		link.setEndIfIndex(ife2.getIndex());
		link.setEndIfDescr(ife2.getDescr());
		link.setEndIp(ife2.getIpAddress());
		link.setEndMac(ife2.getMac());
		// int bw = 0;
		int ifIdx = 1;
		if ((ife1.getSpeed() > 0L) && (ife2.getSpeed() > 0L)) {
			if (ife1.getSpeed() < ife2.getSpeed()) {
				ifIdx = 1;
			} else
				ifIdx = 2;
		} else if (ife1.getSpeed() == 0L) {
			ifIdx = 2;
		}
		if (ifIdx == 1) {
			int bw = (int) (ife1.getSpeed() / 1000000L);
			link.setType(ife1.getTypeName());
			link.setTrafficIf(1);
			link.setTrafficDirect(1);
			link.setBandWidth(bw);
		} else {
			int bw = (int) (ife2.getSpeed() / 1000000L);
			link.setType(ife2.getTypeName());
			link.setTrafficIf(2);
			link.setTrafficDirect(2);
			link.setBandWidth(bw);
		}
		// link.setBandWidth(bw);
		if ((alias == null) || alias.length() == 0) {
			StringBuffer aliasStr = new StringBuffer(50);
			aliasStr.append(host1.getAlias());

			if (!HostConstants.isComputingResDevice(host1.getCategory()))
				aliasStr.append("[").append(ife1.getDescr()).append("]");
			aliasStr.append("--").append(host2.getAlias());
			if (!HostConstants.isComputingResDevice(host2.getCategory()))
				aliasStr.append("[").append(ife2.getDescr()).append("]");
			link.setAlias(aliasStr.toString());
		} else {
			link.setAlias(alias);
		}
		
		link.setId(link.hashCode());
		return link;
	}

	@SuppressWarnings("unused")
	private static String getShortDescr(String descr) {
		if (descr.indexOf("GigabitEthernet") >= 0)
			return descr.replace("GigabitEthernet", "Gi");
		if (descr.indexOf("FastEthernet") >= 0)
			return descr.replace("FastEthernet", "Fa");
		return descr;
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
