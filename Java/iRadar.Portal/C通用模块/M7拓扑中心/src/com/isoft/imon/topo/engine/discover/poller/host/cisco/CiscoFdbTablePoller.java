package com.isoft.imon.topo.engine.discover.poller.host.cisco;

import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.FdbTable;
import com.isoft.imon.topo.engine.discover.bag.host.FdbTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.cisco.VlanCommunityEntry;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.discovery.SnmpDiscovery;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.FdbTablePoller;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 思科Fdb表轮询器
 * 
 * @author Administrator
 * @date 2014年8月7日
 */
public final class CiscoFdbTablePoller extends FdbTablePoller {
	/*
	 * (non-Javadoc) 采集数据
	 * 
	 * @see
	 * com.isoft.engine.discover.poller.host.FdbTablePoller#collect(com.isoft
	 * .engine.discover.element.Host,
	 * com.isoft.engine.discover.discovery.operator.SnmpOperator)
	 */
	@SuppressWarnings("unchecked")
	public FdbTable collect(Host host, SnmpOperator snmp) {
		if (host.getModel().startsWith("SG300")) {
			this.snmp = snmp;
			this.host = host;
			int version = CommonUtil.getSnmpVersionByHost(host);
			return getFdbByWalkQBridge(version);
		}
		FdbTable fdbTable = new FdbTable();
		fdbTable.setElementId(host.getId());
		CompositeBag<VlanCommunityEntry> vces = (CompositeBag<VlanCommunityEntry>) host.getBag(VlanCommunityEntry.class);
		if (vces == null) {
			CiscoVlanCommunityPoller cvcp = new CiscoVlanCommunityPoller();
			vces = cvcp.collect(host, snmp);
			host.putBag(vces);
		}
		int version = CommonUtil.getSnmpVersionByHost(host);
		if (vces == null) {
			List<TableEvent> rows = snmp.createTable(FDB_OIDS, version);
			if (rows != null) {
				for (TableEvent row : rows)
					addEntry(fdbTable, row.getColumns());
			}
			return fdbTable;
		}
		SnmpCredence credence = (SnmpCredence) host.getCredence("SNMP");
		SnmpDiscovery ds = new SnmpDiscovery();
		SnmpCredence _credence = null;
		if (credence != null) {
			_credence = (SnmpCredence) credence.clone();
		}
		for (VlanCommunityEntry vce : vces.getEntities()) {
			if (_credence == null) {
				continue;
			}
			_credence.setCommunity(vce.getCommunity());
			ds.setTarget(host.getIpAddress(), _credence);

			List<TableEvent> rows = ds.createTable(FDB_OIDS, version);
			if (rows != null) {
				for (TableEvent row : rows)
					addEntry(fdbTable, row.getColumns());
			}
		}

		Logger.getLogger(getClass()).info(host.getIpAddress() + ",fdb=" + fdbTable.size());
		return fdbTable;
	}

	/**
	 * 表中添加Entry
	 * 
	 * @param table
	 * @param vb
	 */
	private void addEntry(FdbTable table, VariableBinding[] vb) {
		try {
			String mac = vb[0].getVariable().toString();
			if (mac.length() < 17) {
				mac = NetworkUtil.toHexString(mac);
				Logger.getLogger(getClass()).info("convert fdb mac:" + mac);
			}
			if ((mac.length() == 17) && (!table.contains(mac))) {
				FdbTableEntry entry = new FdbTableEntry();
				entry.setMac(mac);
				entry.setPort(vb[1].getVariable().toString());
				entry.setStatus(1);
				table.add(entry);
			}
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
}
