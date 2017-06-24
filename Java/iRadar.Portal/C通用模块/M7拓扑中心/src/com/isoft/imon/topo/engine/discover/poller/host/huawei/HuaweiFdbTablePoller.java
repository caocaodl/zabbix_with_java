package com.isoft.imon.topo.engine.discover.poller.host.huawei;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.host.FdbTable;
import com.isoft.imon.topo.engine.discover.bag.host.FdbTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.FdbTablePoller;
import com.isoft.imon.topo.host.util.ImsUtil;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.host.util.VarBind;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 华为Fdb表轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public final class HuaweiFdbTablePoller extends FdbTablePoller {
	private static final String[] Q_BRIDGE_FDB = { "1.3.6.1.2.1.17.7.1.2.2.1.2" };
	private static final String HUAWEI_DYN_FDB = "1.3.6.1.4.1.2011.5.25.42.2.1.3.1.4";
	private static final String HUAWEI_FDB_SET = "1.3.6.1.4.1.2011.2.23.1.3.2.1.2";
	private static final String H3C_FDB_SET = "1.3.6.1.4.1.25506.8.35.3.2.1.2";
	private static final Map<String, Method> methods;

	static {
		HuaweiPollerHelper helper = new HuaweiPollerHelper();
		methods = helper.getMethods(HuaweiFdbTablePoller.class, "fdb");
	}

	/* (non-Javadoc)
	 * 采集数据
	 * @see com.isoft.engine.discover.poller.host.FdbTablePoller#collect(com.isoft.engine.discover.element.Host, com.isoft.engine.discover.discovery.operator.SnmpOperator)
	 */
	public FdbTable collect(Host host, SnmpOperator snmp) {
		this.host = host;
		this.snmp = snmp;
		int version = CommonUtil.getSnmpVersionByHost(host);
		FdbTable table = null;
		try {
			if (methods.containsKey(host.getSysOid()))
				table = (FdbTable) ((Method) methods.get(host.getSysOid()))
						.invoke(this, new Integer(0));
			else
				table = getFdbTable(version);
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
	 * 获取Fdb表
	 * @param version
	 * @return
	 */
	private FdbTable getFdbTable(int version) {

		FdbTable table = getFdbByWalkBridge(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		table = getFdbByWalkQBridge(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		table = getFdbByQBridge(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		table = getFdbByFdbSet(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		table = getFdbByH3CFdbSet(version);
		if (!ImsUtil.isEmpty(table)) {
			return table;
		}
		table = getFdbByWalkDynFdb(version);
		if (!ImsUtil.isEmpty(table))
			return table;
		Logger.getLogger(getClass()).info(
				this.host.getIpAddress() + "(" + this.host.getSysOid()
						+ ") can not find match FDB");
		return null;
	}

	/**
	 * 通过Q桥接获取Fdb
	 * @param version
	 * @return
	 */
	private FdbTable getFdbByQBridge(int version) {
		List<TableEvent> rows = this.snmp.createTable(Q_BRIDGE_FDB,version);
		if (CommonUtil.isEmpty(rows))
			return null;

		FdbTable table = new FdbTable();
		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if ((vb == null) || (vb[0] == null)) {
				continue;
			}
			String temp = vb[0].toString();
			int loc = temp.indexOf(".", Q_BRIDGE_FDB[0].length() + 1) + 1;
			temp = temp.substring(loc);
			String[] _temps = temp.split(" = ");
			String mac = NetworkUtil.getHexAddress(_temps[0]);
			if ((mac.length() != 17) || (table.contains(mac))) {
				continue;
			}
			FdbTableEntry entry = new FdbTableEntry();
			entry.setMac(mac);
			entry.setPort(_temps[1]);
			entry.setStatus(2);
			table.add(entry);
		}
		Logger.getLogger(getClass()).info(
				this.host.getIpAddress() + "(" + this.host.getSysOid()
						+ ",getFdbByQBridge),size=" + table.size());
		return table;
	}

	/**
	 * 通过walk方式获取fdb
	 * @param version
	 * @return
	 */
	private FdbTable getFdbByWalkDynFdb(int version) {
		List<VarBind> rows = this.snmp.getWalkTable(HUAWEI_DYN_FDB,version);
		if (CommonUtil.isEmpty(rows))
			return null;

		FdbTable table = new FdbTable();
		for (VarBind row : rows) {
			String mac = NetworkUtil.getHexAddress(row.getOid());
			if (mac.length() > 17)
				mac = mac.substring(0, 17);
			if (table.contains(mac)) {
				continue;
			}
			FdbTableEntry entry = new FdbTableEntry();
			entry.setMac(mac);
			entry.setPort(row.getValue());
			entry.setStatus(6);
			table.add(entry);
		}
		Logger.getLogger(getClass()).info(
				this.host.getIpAddress() + "(" + this.host.getSysOid()
						+ ",getFdbByWalkDynFdb),size=" + table.size());
		return table;
	}

	/**
	 * 通过fdb集合获取fdb表
	 * @param version
	 * @return
	 */
	private FdbTable getFdbByFdbSet(int version) {
		List<VarBind> rows = this.snmp.getWalkTable(HUAWEI_FDB_SET,version);
		if (CommonUtil.isEmpty(rows)) {
			return null;
		}
		FdbTable table = new FdbTable();
		for (VarBind row : rows) {
			int loc = row.getOid().indexOf(".") + 1;
			String mac = NetworkUtil.getHexAddress(row.getOid().substring(loc));
			if (table.contains(mac)) {
				continue;
			}
			FdbTableEntry entry = new FdbTableEntry();
			entry.setMac(mac);
			entry.setStatus(4);
			entry.setPort(row.getValue());
			table.add(entry);
		}
		Logger.getLogger(getClass()).info(
				this.host.getIpAddress() + "(" + this.host.getSysOid()
						+ ",getFdbByFdbSet),size=" + table.size());
		return table;
	}

	/**
	 * 通过H3Cfdb集合获取fdb
	 * @param version
	 * @return
	 */
	private FdbTable getFdbByH3CFdbSet(int version) {
		List<VarBind> rows = this.snmp.getWalkTable(H3C_FDB_SET,version);
		if (CommonUtil.isEmpty(rows)) {
			return null;
		}
		FdbTable table = new FdbTable();
		for (VarBind row : rows) {
			int loc = row.getOid().indexOf(".") + 1;
			String mac = NetworkUtil.getHexAddress(row.getOid().substring(loc));
			if (table.contains(mac)) {
				continue;
			}
			FdbTableEntry entry = new FdbTableEntry();
			entry.setMac(mac);
			entry.setStatus(5);
			entry.setPort(row.getValue());
			table.add(entry);
		}
		Logger.getLogger(getClass()).info(
				this.host.getIpAddress() + "(" + this.host.getSysOid()
						+ ",getFdbByH3CFdbSet),size=" + table.size());
		return table;
	}
}
