package com.isoft.imon.topo.engine.discover.poller.host;

import java.util.List;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.StpTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.cisco.VlanCommunityEntry;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.discovery.SnmpDiscovery;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.cisco.CiscoVlanCommunityPoller;
import com.isoft.imon.topo.util.CommonUtil;

/**stp表轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public class StpTablePoller extends SnmpPoller {
	private static final String[] OIDS = { "1.3.6.1.2.1.17.2.15.1.1",
			"1.3.6.1.2.1.17.2.15.1.3", "1.3.6.1.2.1.17.2.15.1.4",
			"1.3.6.1.2.1.17.2.15.1.8", "1.3.6.1.2.1.17.2.15.1.9" };

	/* (non-Javadoc)采集数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CompositeBag<StpTableEntry> collect(Host host, SnmpOperator snmp) {
		CompositeBag table = new CompositeBag(host.getId(), getBagName());
		int version = CommonUtil.getSnmpVersionByHost(host);
		if (!"cisco".equals(host.getSymbol())) {
			List rows = snmp.createTable(OIDS,version);
			fillTable(snmp, table, rows);
			return table;
		}
		CompositeBag<VlanCommunityEntry> vces = (CompositeBag) host
				.getBag(VlanCommunityEntry.class);
		if (vces == null) {
			CiscoVlanCommunityPoller cvcp = new CiscoVlanCommunityPoller();
			vces = cvcp.collect(host, snmp);
			host.putBag(vces);
		}
		if (vces == null) {
			List rows = snmp.createTable(OIDS,version);
			fillTable(snmp, table, rows);
			return table;
		}
		SnmpDiscovery ds = new SnmpDiscovery();
		SnmpCredence credence = (SnmpCredence) host.getCredence("SNMP");
		SnmpCredence _credence = null;
		if(credence != null){
			 _credence = (SnmpCredence) credence.clone();
		}
		for (VlanCommunityEntry vce : vces.getEntities()) {
			if(_credence == null){
				continue;
			}
			_credence.setCommunity(vce.getCommunity());
			ds.setTarget(host.getIpAddress(), _credence);
			List rows = ds.createTable(OIDS,version);
			fillTable(snmp, table, rows);
		}
		return table;
	}

	/**
	 * 填充表
	 * @param snmp
	 * @param table
	 * @param rows
	 */
	private void fillTable(SnmpOperator snmp, CompositeBag<StpTableEntry> table,
			List<TableEvent> rows) {
		if (rows == null)
			return;

		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if ((vb == null) || ("00:00".equals(snmp.getValue(vb[4])))
					|| ("00:00:00:00:00:00:00:00".equals(snmp.getValue(vb[3])))) {
				continue;
			}
			StpTableEntry entry = new StpTableEntry();
			entry.setPort(snmp.getValue(vb[0]));
			entry.setState(snmp.getIntValue(vb[1]));
			String str = snmp.getValue(vb[3]);
			if(str == null){
				continue;
			}
			entry.setDesignatedBridge(str.substring(6));
			entry.setDesignatedPort(snmp.getValue(vb[4]));
			if (!table.contains(entry))
				table.add(entry);
		}
	}
}
