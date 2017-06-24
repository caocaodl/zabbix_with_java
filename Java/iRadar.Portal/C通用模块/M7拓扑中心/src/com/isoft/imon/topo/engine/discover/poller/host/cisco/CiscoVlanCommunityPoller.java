package com.isoft.imon.topo.engine.discover.poller.host.cisco;

import java.util.List;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.Credence;
import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.cisco.VlanCommunityEntry;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.SnmpPoller;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 思科Vlan共同体轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public final class CiscoVlanCommunityPoller extends SnmpPoller {
	private static final String[] ENT_LOGICAL_TABLE = {
			"1.3.6.1.2.1.47.1.2.1.1.3", "1.3.6.1.2.1.47.1.2.1.1.4" };
	private static final String ENT_LOGICAL_TYPE = "1.3.6.1.2.1.17";

	/* (non-Javadoc)
	 * 采集数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	public CompositeBag<VlanCommunityEntry> collect(Host host, SnmpOperator snmp) {
		int version = CommonUtil.getSnmpVersionByHost(host);
		List<TableEvent> rows = snmp.createTable(ENT_LOGICAL_TABLE,version);
		if (rows == null)
			return null;

		CompositeBag<VlanCommunityEntry> bag = new CompositeBag<VlanCommunityEntry>(host.getId(), getBagName());
		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if ((vb[0] != null)
					&& (vb[0].getVariable().toString().equals(ENT_LOGICAL_TYPE))) {
				VlanCommunityEntry entry = new VlanCommunityEntry();
				entry.setCommunity(vb[1].getVariable().toString());
				bag.add(entry);
			}
		}
		Credence credence = host.getCredence("SNMP");
		if(credence == null){
			return null;
		}
		String community = ((SnmpCredence) credence)
				.getCommunity();
		VlanCommunityEntry defualEntry = new VlanCommunityEntry();
		defualEntry.setCommunity(community);
		bag.add(defualEntry);
		return bag;
	}
}
