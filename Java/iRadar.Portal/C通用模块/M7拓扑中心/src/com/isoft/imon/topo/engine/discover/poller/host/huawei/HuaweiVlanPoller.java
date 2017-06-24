package com.isoft.imon.topo.engine.discover.poller.host.huawei;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.PortTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.VlanTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.VlanPoller;
import com.isoft.imon.topo.engine.discover.poller.host.IfTablePoller;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.host.util.ImsUtil;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 华为Vlan轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public final class HuaweiVlanPoller extends VlanPoller {
	private static final Map<String, Method> methods;
	private Host host;
	private SnmpOperator snmp;

	static {
		HuaweiPollerHelper helper = new HuaweiPollerHelper();
		methods = helper.getMethods(HuaweiVlanPoller.class, "vlan");
	}

	/* (non-Javadoc)
	 * 采集数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public CompositeBag<VlanTableEntry> collect(Host host, SnmpOperator snmp) {
		this.host = host;
		this.snmp = snmp;
		init(host, snmp);
		int version = CommonUtil.getSnmpVersionByHost(host);
		CompositeBag<VlanTableEntry> table = null;
		try {
			if (methods.containsKey(host.getSysOid()))
				table = (CompositeBag<VlanTableEntry>) ((Method) methods.get(host.getSysOid()))
						.invoke(this, new Integer(0));
			else
				table = getVlanTable(version);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!ImsUtil.isEmpty(table)) {
			table.setElementId(host.getId());
			return table;
		}
		return null;
	}

	/**
	 * 获取Vlan表
	 * @param version
	 * @return
	 */
	private CompositeBag<VlanTableEntry> getVlanTable(int version) {
		CompositeBag<VlanTableEntry> table = getVlanByQBridge(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		table = getVlanByHuaweiLsw(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		table = getVlanByH3CRfc1155(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		table = getVlanByHuaweiRfc1155(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		Logger.getLogger(getClass()).info(
				this.host.getIpAddress() + "(" + this.host.getSysOid()
						+ ") can not find match VLAN");
		return null;
	}

	/**
	 * 通过华为Lsw获取Vlan
	 * @param version
	 * @return
	 */
	private CompositeBag<VlanTableEntry> getVlanByHuaweiLsw(int version) {
		String[][] rows = this.snmp.getWalkTable(HostConstants.HUAWEI_LSW_VLAN,version);
		if (CommonUtil.isEmpty(rows))
			return null;

		CompositeBag<VlanTableEntry> table = new CompositeBag<VlanTableEntry>(this.host.getId(), getBagName());
		for (String[] row : rows) {
			VlanTableEntry entry = new VlanTableEntry();
			entry.setVlan(row[0]);
			entry.setAlias(row[1]);
			entry.setMemberIfs(getMemberIfs(this.host, row[2]));
			entry.setType(Integer.parseInt(row[3]));
			entry.setState(Integer.parseInt(row[4]));
			IfTableEntry ife = getVlanIfe(this.host, entry.getVlan());
			if ((ife != null) && (ife.getIpAddress() != null)) {
				entry.setNetAddress(NetworkUtil.getNetAddress(ife
						.getFirstIpAddress(), ife.getMask()));
				entry.setNetMask(ife.getMask());
			}
			table.add(entry);
		}
		Logger.getLogger(getClass()).info(
				this.host.getIpAddress() + "(" + this.host.getSysOid()
						+ "),getVlanByHuaweiLsw=" + table.size());
		return table;
	}

	/**
	 * 通过Q桥接获取Vlan
	 * @param version
	 * @return
	 */
	private CompositeBag<VlanTableEntry> getVlanByQBridge(int version) {
		List<TableEvent> rows = this.snmp.createTable(HostConstants.Q_BRIDGE_VLAN,version);
		if (CommonUtil.isEmpty(rows))
			return null;

		CompositeBag<VlanTableEntry> table = new CompositeBag<VlanTableEntry>(this.host.getId(), getBagName());
		for (TableEvent event : rows) {
			VariableBinding[] vb = event.getColumns();

			VlanTableEntry entry = new VlanTableEntry();
			entry.setVlan(this.snmp.getValue(vb[0]));
			entry.setMemberIfs(getMemberIfs(this.host, this.snmp
					.getValue(vb[1])));
			entry.setType(this.snmp.getIntValue(vb[2]));
			entry.setAlias("Vlan" + entry.getVlan());
			IfTableEntry ife = getVlanIfe(this.host, entry.getVlan());
			if ((ife != null) && (ife.getIpAddress() != null)) {
				entry.setNetAddress(NetworkUtil.getNetAddress(ife
						.getFirstIpAddress(), ife.getMask()));
				entry.setNetMask(ife.getMask());
			}
			table.add(entry);
		}
		Logger.getLogger(getClass()).info(
				this.host.getIpAddress() + "(" + this.host.getSysOid()
						+ "),getVlanByQBridge=" + table.size());
		return table;
	}

	/**
	 * 通过H3CRfc1155获取Vlan
	 * @param version
	 * @return
	 */
	private CompositeBag<VlanTableEntry> getVlanByH3CRfc1155(int version) {
		return getVlanByRfc1155(HostConstants.H3C_RFC1155_VLAN,version);
	}

	/**
	 * 通过华为Rfc1155获取Vlan
	 * @param version
	 * @return
	 */
	private CompositeBag<VlanTableEntry> getVlanByHuaweiRfc1155(int version) {
		return getVlanByRfc1155(HostConstants.HUAWEI_RFC1155_VLAN,version);
	}

	/**
	 * 通过Rfc1155获取Vlan
	 * @param oids
	 * @param version
	 * @return
	 */
	private CompositeBag<VlanTableEntry> getVlanByRfc1155(String[] oids,int version) {
		List<TableEvent> rows = this.snmp.createTable(oids,version);
		if (CommonUtil.isEmpty(rows))
			return null;

		CompositeBag<VlanTableEntry> table = new CompositeBag<VlanTableEntry>(this.host.getId(), getBagName());
		for (TableEvent event : rows) {
			VariableBinding[] vb = event.getColumns();
			if (vb == null)
				continue;
			VlanTableEntry entry = new VlanTableEntry();
			entry.setVlan(this.snmp.getValue(vb[0]));
			entry.setAlias(this.snmp.getValue(vb[1]));
			entry.setMemberIfs(getMemberIfs(this.host, this.snmp
					.getValue(vb[2])));
			entry.setType(this.snmp.getIntValue(vb[3]));
			entry.setState(this.snmp.getIntValue(vb[4]));
			IfTableEntry ife = getVlanIfe(this.host, entry.getVlan());
			if ((ife != null) && (ife.getIpAddress() != null)) {
				entry.setNetAddress(NetworkUtil.getNetAddress(ife
						.getFirstIpAddress(), ife.getMask()));
				entry.setNetMask(ife.getMask());
			}
			table.add(entry);
		}
		if (oids[0].startsWith("1.3.6.1.4.1.2011."))
			Logger.getLogger(getClass()).info(
					this.host.getIpAddress() + "(" + this.host.getSysOid()
							+ "),getVlanByHuaweiRfc1155=" + table.size());
		else if (oids[0].startsWith("1.3.6.1.4.1.25506."))
			Logger.getLogger(getClass()).info(
					this.host.getIpAddress() + "(" + this.host.getSysOid()
							+ "),getVlanByH3CRfc1155=" + table.size());
		return table;
	}

	/* (non-Javadoc)
	 * 初始化
	 * @see com.isoft.engine.discover.poller.VlanPoller#init(com.isoft.engine.discover.element.Host, com.isoft.engine.discover.discovery.operator.SnmpOperator)
	 */
	protected void init(Host host, SnmpOperator snmp) {
		if (host.getBag(PortTableEntry.class) == null) {
			HuaweiPortTablePoller poller = new HuaweiPortTablePoller();
			host.putBag(poller.collect(host, snmp));
		}
		if (host.getBag(IfTableEntry.class) == null) {
			IfTablePoller poller = new IfTablePoller();
			host.putBag(poller.collect(host, snmp));
		}
	}
}
