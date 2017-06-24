package com.isoft.imon.topo.engine.discover.poller.host.cisco;

import java.util.HashMap;
import java.util.Map;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.VlanTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.cisco.VlanCommunityEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.VlanPoller;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 思科Vlan轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public final class CiscoVlanPoller extends VlanPoller {
	/* (non-Javadoc)
	 * 采集数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CompositeBag<VlanTableEntry> collect(Host host, SnmpOperator snmp) {
		CompositeBag<VlanTableEntry> bag = new CompositeBag<VlanTableEntry>(host.getId(), getBagName());
		int version = CommonUtil.getSnmpVersionByHost(host);
		String[][] table1 = snmp.getWalkTable(HostConstants.VALN_MEMBER_OIDS,version);
		String[][] table2 = snmp.getWalkTable(HostConstants.VLAN_OIDS,version);
		if ((table1 == null) || (table2 == null))
			return null;

		init(host, snmp);

		Map memberships = new HashMap();
		for (String[] row : table1)
			memberships.put(row[0], getMemberIfs(host, row[1]));
		for (int i = 0; i < table2.length; i++) {
			VlanTableEntry entry = new VlanTableEntry();
			entry.setVlan(table2[i][0].split("\\.")[1]);
			entry.setState(Integer.parseInt(table2[i][1]));
			entry.setType(Integer.parseInt(table2[i][2]));
			entry.setAlias(CommonUtil.getRidQuote(table2[i][3]));
			entry.setMemberIfs((String) memberships.get(entry.getVlan()));
			IfTableEntry ife = host.getIfByDescr("Vlan" + entry.getVlan());
			if ((ife != null) && (ife.getIpAddress() != null)) {
				entry.setNetAddress(NetworkUtil.getNetAddress(ife
						.getFirstIpAddress(), ife.getMask()));
				entry.setNetMask(ife.getMask());
			}
			bag.add(entry);
		}
		return bag;
	}

	/* (non-Javadoc)
	 * 初始化
	 * @see com.isoft.engine.discover.poller.VlanPoller#init(com.isoft.engine.discover.element.Host, com.isoft.engine.discover.discovery.operator.SnmpOperator)
	 */
	protected void init(Host host, SnmpOperator snmp) {
		super.init(host, snmp);
		if (host.getBag(VlanCommunityEntry.class) == null) {
			CiscoVlanCommunityPoller poller = new CiscoVlanCommunityPoller();
			host.putBag(poller.collect(host, snmp));
		}
	}
}
