package com.isoft.imon.topo.engine.discover.poller.host;

import java.util.List;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.PortTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.cisco.VlanCommunityEntry;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.discovery.SnmpDiscovery;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.cisco.CiscoVlanCommunityPoller;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 端口表轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public class PortTablePoller extends SnmpPoller {
	/* (non-Javadoc)
	 * 采集端口数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CompositeBag<PortTableEntry> collect(Host host, SnmpOperator snmp) {
		CompositeBag<PortTableEntry> table = new CompositeBag<PortTableEntry>(host.getId(), getBagName());
		int version = CommonUtil.getSnmpVersionByHost(host);
		if (!"cisco".equals(host.getSymbol())) {
			List<TableEvent> rows = snmp.createTable(HostConstants.PORT_OIDS,version);
			fillTable(snmp, table, rows);
			return table;
		}
		CompositeBag<VlanCommunityEntry> vces = (CompositeBag<VlanCommunityEntry>) host
				.getBag(VlanCommunityEntry.class);
		if (vces == null) {
			CiscoVlanCommunityPoller cvcp = new CiscoVlanCommunityPoller();
			vces = cvcp.collect(host, snmp);
			host.putBag(vces);
		}
		if (vces == null) {
			List rows = snmp.createTable(HostConstants.PORT_OIDS,version);
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
			List rows = ds.createTable(HostConstants.PORT_OIDS,version);
			fillTable(snmp, table, rows);
		}
		return table;
	}

	/**
	 * 填充表数据
	 * @param snmp
	 * @param table
	 * @param rows
	 */
	private void fillTable(SnmpOperator snmp, CompositeBag<PortTableEntry> table,
			List<TableEvent> rows) {
		if (rows == null)
			return;

		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if (vb == null)
				continue;
			PortTableEntry entry = new PortTableEntry();
			entry.setPort(snmp.getValue(vb[0]));
			entry.setIfIndex(snmp.getValue(vb[1]));
			if (!table.contains(entry))
				table.add(entry);
		}
	}
}
