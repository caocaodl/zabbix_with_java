package com.isoft.imon.topo.engine.discover.poller.host;

import java.util.List;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.ArpTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.util.CommonUtil;

/**ARP表轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public final class ArpTablePoller extends SnmpPoller {

	/* (non-Javadoc)
	 * 采集数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	public CompositeBag<ArpTableEntry> collect(Host host, SnmpOperator snmp) {
		int version = CommonUtil.getSnmpVersionByHost(host);
		List<TableEvent> rows = snmp.createTable(HostConstants.ARP_OIDS,version);
		if (rows == null || rows.isEmpty()) {
			return null;
		}

		CompositeBag<ArpTableEntry> table = new CompositeBag<ArpTableEntry>(host.getId(), getBagName());
		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if (vb == null) {
				continue;
			}
			String mac = vb[1].getVariable().toString();
			if (mac.length() < 17) {
				mac = NetworkUtil.toHexString(mac);
			}

			if (mac.length() != 17) {
				continue;
			}
			ArpTableEntry entry = new ArpTableEntry();
			entry.setIfIndex(snmp.getValue(vb[0]));
			entry.setMac(mac);
			entry.setIpAddress(snmp.getValue(vb[2]));
			entry.setType(snmp.getIntValue(vb[3]));
			table.add(entry);
		}
		return table;
	}
}
