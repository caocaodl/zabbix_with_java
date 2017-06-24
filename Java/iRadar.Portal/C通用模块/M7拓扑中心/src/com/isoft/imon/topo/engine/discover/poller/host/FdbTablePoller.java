package com.isoft.imon.topo.engine.discover.poller.host;

import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.host.FdbTable;
import com.isoft.imon.topo.engine.discover.bag.host.FdbTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.host.util.VarBind;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * FDB表轮询器
 * 
 * @author Administrator
 * @date 2014年8月7日
 */
public class FdbTablePoller extends SnmpPoller {
	protected static final int Q_BRIDGE = 2;
	protected static final int Q_BRIDGE_BY_WALK = 3;
	protected static final int HUAWEI_FDB_SET_WALK = 4;
	protected static final int H3C_FDB_SET_WALK = 5;
	protected static final int HUAWEI_DYN_BY_WALK = 6;
	protected static final int BRIDGE = 1;
	protected SnmpOperator snmp;
	protected Host host;
	protected static final String[] FDB_OIDS = { "1.3.6.1.2.1.17.4.3.1.1", "1.3.6.1.2.1.17.4.3.1.2", "1.3.6.1.2.1.17.4.3.1.3" };
	private static final String Q_BRIDGE_FDB = "1.3.6.1.2.1.17.7.1.2.2.1.2";

	/*
	 * (non-Javadoc) 采集数据
	 * 
	 * @see
	 * com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement
	 * , java.lang.Object)
	 */
	public FdbTable collect(Host host, SnmpOperator snmp) {
		this.snmp = snmp;
		this.host = host;
		int version = CommonUtil.getSnmpVersionByHost(host);
		return getFdbByWalkBridge(version);
	}

	/**
	 * 通过walk桥接获取FDB表
	 * 
	 * @param version
	 * @return
	 */
	protected FdbTable getFdbByWalkBridge(int version) {
		List<VarBind> rows = this.snmp.getWalkTable(FDB_OIDS[1], version);
		if (CommonUtil.isEmpty(rows)) {
			return null;
		}
		Logger.getLogger(getClass()).info(this.host.getIpAddress() + ",fdb=" + rows.size());
		FdbTable table = new FdbTable();
		table.setElementId(this.host.getId());
		for (VarBind vb : rows) {
			String mac = NetworkUtil.getHexAddress(vb.getOid());
			if ((mac.length() != 17) || (table.contains(mac))) {
				continue;
			}
			FdbTableEntry entry = new FdbTableEntry();
			entry.setMac(mac);
			entry.setStatus(1);
			entry.setPort(vb.getValue());
			table.add(entry);
		}
		return table;
	}

	/**
	 * 获取FDB表
	 * 
	 * @param version
	 * @return
	 */
	protected FdbTable getFdbByWalkQBridge(int version) {
		List<VarBind> rows = this.snmp.getWalkTable(Q_BRIDGE_FDB, version);
		if (CommonUtil.isEmpty(rows))
			return null;

		FdbTable table = new FdbTable();
		for (VarBind row : rows) {
			String temp = row.getOid().substring(row.getOid().indexOf(".") + 1);
			String mac = NetworkUtil.getHexAddress(temp);
			if ((mac.length() != 17) || (table.contains(mac))) {
				continue;
			}
			FdbTableEntry entry = new FdbTableEntry();
			entry.setMac(mac);
			entry.setPort(row.getValue());
			entry.setStatus(3);
			table.add(entry);
		}
		Logger.getLogger(getClass()).info(this.host.getIpAddress() + "(" + this.host.getSysOid() + ",getFdbByWalkQBridge),size=" + table.size());
		return table;
	}

	/**
	 * 通过桥接获取FDB表
	 * 
	 * @param version
	 * @return
	 */
	protected FdbTable getFdbByBridge(int version) {
		List<TableEvent> rows = this.snmp.createTable(FDB_OIDS, version);
		if (CommonUtil.isEmpty(rows)) {
			return null;
		}
		Logger.getLogger(getClass()).info(this.host.getIpAddress() + ",Bridge fdb=" + rows.size());
		FdbTable table = new FdbTable();
		table.setElementId(this.host.getId());
		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if (vb == null)
				continue;
			String mac = this.snmp.getValue(vb[0]);
			if (mac == null) {
				continue;
			}
			if ((mac.length() != 17) || (table.contains(mac))) {
				continue;
			}
			FdbTableEntry entry = new FdbTableEntry();
			entry.setMac(mac);
			entry.setPort(this.snmp.getValue(vb[1]));
			entry.setStatus(1);
			table.add(entry);
		}
		return table;
	}
}
