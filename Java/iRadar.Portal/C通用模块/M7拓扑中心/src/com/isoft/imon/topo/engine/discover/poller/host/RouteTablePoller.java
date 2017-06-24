package com.isoft.imon.topo.engine.discover.poller.host;

import java.util.List;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.RouteTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 路由表轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public class RouteTablePoller extends SnmpPoller {
	private static final String[] OIDS = { "1.3.6.1.2.1.4.21.1.1",
			"1.3.6.1.2.1.4.21.1.2", "1.3.6.1.2.1.4.21.1.3",
			"1.3.6.1.2.1.4.21.1.7", "1.3.6.1.2.1.4.21.1.8",
			"1.3.6.1.2.1.4.21.1.9", "1.3.6.1.2.1.4.21.1.11" };

	/* (non-Javadoc)
	 * 采集路由数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	public CompositeBag<RouteTableEntry> collect(Host host, SnmpOperator snmp) {
		int version = CommonUtil.getSnmpVersionByHost(host);
		List<TableEvent> rows = snmp.createTable(getOids(),version);
		if (rows == null){
			return null;
		}
		CompositeBag<RouteTableEntry> table = new CompositeBag<RouteTableEntry>(host.getId(), getBagName());
		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if (vb == null)
				continue;
			RouteTableEntry entry = new RouteTableEntry();
			entry.setDest(snmp.getValue(vb[0]));
			entry.setIfIndex(snmp.getValue(vb[1]));
			entry.setMetric(snmp.getIntValue(vb[2]));
			entry.setNextHop(snmp.getValue(vb[3]));
			entry.setType(snmp.getIntValue(vb[4]));
			entry.setProto(snmp.getIntValue(vb[5]));
			entry.setMask(snmp.getValue(vb[6]));
			table.add(entry);
		}
		return table;
	}

	/**
	 * 获取OID
	 * @return
	 */
	protected String[] getOids() {
		return OIDS;
	}
}
