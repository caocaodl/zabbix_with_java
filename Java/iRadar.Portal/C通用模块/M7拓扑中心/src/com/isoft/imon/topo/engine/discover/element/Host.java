package com.isoft.imon.topo.engine.discover.element;

import java.util.ArrayList;
import java.util.List;

import com.isoft.imon.topo.admin.factory.DictionaryEntry;
import com.isoft.imon.topo.admin.factory.DictionaryFactory;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.Sniffer;
import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.IfTable;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.PortTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.RouteTableEntry;
import com.isoft.imon.topo.engine.discover.sniffer.SnmpHostSniffer;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 主机类
 * 
 * @author Administrator
 * 
 * @date 2014年8月7日
 */
public class Host extends NetElement {

	private String enterprise;
	private String model;
	private String sysOid;
	private String sysName;
	private String sysDescr;
	private String bridgeMac;
	private String serialNum;
	private List<Subnet> subnets;
	private String tenantId;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("HOST");
		sb.append(super.toString());
		sb.deleteCharAt(sb.length()-1).append(", ");
		sb.append(
			"enterprise="+this.enterprise+", " +
			"model="+this.model+", " +
			"sysOid="+this.sysOid+", " +
			"sysName="+this.sysName+", " +
			"sysDescr="+this.sysDescr+", " +
			"bridgeMac="+this.bridgeMac+", " +
			"serialNum="+this.serialNum+", " +
			"subnets="+this.subnets+", " +
			"tenantId="+this.tenantId
		);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 获取子网集合
	 * 
	 * @return
	 */
	public List<Subnet> getSubnets() {
		return this.subnets;
	}

	/**
	 * 获取子网总数
	 * 
	 * @return
	 */
	public int getSubnetTotal() {
		if (this.subnets == null)
			return 0;
		return this.subnets.size();
	}

	/**
	 * 添加子网
	 * 
	 * @param subnet
	 */
	public void addSubnet(Subnet subnet) {
		if (subnet == null)
			return;

		if (this.subnets == null)
			this.subnets = new ArrayList<Subnet>();
		if (!this.subnets.contains(subnet))
			this.subnets.add(subnet);
	}

	
	/**
	 * 获取厂商
	 * 
	 * @return
	 */
	public String getEnterprise() {
		return enterprise(this.enterprise);
	}

	/**
	 * 获取厂商
	 * 
	 * @param v
	 * @return
	 */
	public static String enterprise(String v) {
		if (!CommonUtil.isEmpty(v)) {
			boolean contains = false;

			List<DictionaryEntry> enterprises = DictionaryFactory.getFactory().getEntries("enterprise");
			for (DictionaryEntry entry : enterprises) {
				if (v.equals(entry.getKey())) {
					contains = true;
					break;
				}
			}

			if (contains) {
				return v;
			}
		}
		return "Unknown";
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
	 * 获取桥连接MAC地址
	 * 
	 * @return
	 */
	public String getBridgeMac() {
		return this.bridgeMac;
	}

	/**
	 * 设置桥连接MA地址
	 * 
	 * @param bridgeMac
	 */
	public void setBridgeMac(String bridgeMac) {
		this.bridgeMac = bridgeMac;
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
		if (model != null)
			this.model = model;
	}

	/**
	 * 获取系统OID
	 * 
	 * @return
	 */
	public String getSysOid() {
		return this.sysOid;
	}

	/**
	 * 设置系统OID
	 * 
	 * @param sysOid
	 */
	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}

	/**
	 * 获取系统名称
	 * 
	 * @return
	 */
	public String getSysName() {
		return this.sysName;
	}

	/**
	 * 设置系统名称
	 * 
	 * @param sysName
	 */
	public void setSysName(String sysName) {
		this.sysName = sysName;
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

	/**
	 * 获取接口表
	 * 
	 * @return
	 */
	public IfTable getIfTable() {
		return (IfTable) this.getBag(IfTableEntry.class);
	}

	/**
	 * 通过IP地址获取接口
	 * 
	 * @param ip
	 * @return
	 */
	public IfTableEntry getIfByIP(String ip) {
		IfTable it = getIfTable();
		if (it == null) {
			return null;
		}
		return it.getIfByIP(ip);
	}

	/**
	 * 通过索引获取接口
	 * 
	 * @param index
	 * @return
	 */
	public IfTableEntry getIfByIndex(String index) {
		IfTable it = getIfTable();
		if (it == null) {
			return null;
		}
		return it.getIfByIndex(index);
	}

	/**
	 * 通过描述获取接口
	 * 
	 * @param descr
	 * @return
	 */
	public IfTableEntry getIfByDescr(String descr) {
		IfTable it = getIfTable();
		if (it == null) {
			return null;
		}
		return it.getIfByDescr(descr);
	}

	/**
	 * 通过MAC地址获取接口
	 * 
	 * @param mac
	 * @return
	 */
	public IfTableEntry getIfByMac(String mac) {
		IfTable it = getIfTable();
		if (it == null) {
			return null;
		}
		return it.getIfByMac(mac);
	}

	/**
	 * 获取序列号
	 * 
	 * @return
	 */
	public String getSerialNum() {
		return this.serialNum;
	}

	/**
	 * 设置序列号
	 * 
	 * @param serialNum
	 */
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	/**
	 * 通过子网地址获取接口
	 * 
	 * @param netAddress
	 * @return
	 */
	public IfTableEntry getIfBySubnet(String netAddress) {
		CompositeBag<?> routeTable = (CompositeBag<?>) getBag(RouteTableEntry.class);
		if (routeTable != null) {
			RouteTableEntry rte = (RouteTableEntry) routeTable.getEntry("dest", netAddress);

			IfTable ift = getIfTable();
			if ((rte == null) || (ift == null)) {
				return null;
			}

			return ift.getIfByIndex(rte.getIfIndex());
		}
		return null;
	}

	/**
	 * 通过端口获取接口
	 * 
	 * @param port
	 * @return
	 */
	public IfTableEntry getIfByPort(String port) {
		CompositeBag<?> bpTable = (CompositeBag<?>) getBag(PortTableEntry.class);
		if (bpTable != null) {
			PortTableEntry bpte = (PortTableEntry) bpTable.getEntry("port", port);
			IfTable ift = getIfTable();
			if ((bpte == null) || (ift == null)) {
				return null;
			}

			return ift.getIfByIndex(bpte.getIfIndex());
		}
		return null;
	}

	/*
	 * 获取嗅探器类 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.NetElement#getSnifferClazz()
	 */
	public Class<? extends Sniffer> getSnifferClazz() {
		return SnmpHostSniffer.class;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
